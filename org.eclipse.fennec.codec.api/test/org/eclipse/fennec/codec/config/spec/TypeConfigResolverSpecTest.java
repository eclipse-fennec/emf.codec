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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.TypeConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Spec-based resolver tests for Type configuration.
 * <p>
 * Verifies that {@link ConfigurationResolver#resolveTypeConfig} applies the
 * two-dimensional resolution (Source Hierarchy × Scope Chain) correctly
 * for Type-specific properties.
 * <p>
 * Note: Generic resolution mechanics (shared by all config types) are tested in
 * {@link ConfigurationResolverSpecTest} using TypeConfig as representative example.
 * This file focuses on Type-specific properties, validation, and behavior.
 * <p>
 * Spec references:
 * <ul>
 *   <li>{@code docs/codec-v2-spec/02-config-resolution.md} - Configuration Resolution</li>
 *   <li>{@code docs/codec-v2-spec/06-type.md} - Type Serialization (§4, §5.0, §6, §7)</li>
 * </ul>
 */
@DisplayName("Type Configuration Resolver Spec Tests (spec 06-type.md)")
class TypeConfigResolverSpecTest {

    private DiagnosticCollector diagnostics;
    private EClass personClass;
    private EClass addressClass;

    @BeforeEach
    void setUp() {
        diagnostics = new DiagnosticCollector();

        EPackage testPackage = EcoreFactory.eINSTANCE.createEPackage();
        testPackage.setName("test");
        testPackage.setNsPrefix("test");
        testPackage.setNsURI("http://test.org/1.0");

        personClass = EcoreFactory.eINSTANCE.createEClass();
        personClass.setName("Person");
        testPackage.getEClassifiers().add(personClass);

        addressClass = EcoreFactory.eINSTANCE.createEClass();
        addressClass.setName("Address");
        testPackage.getEClassifiers().add(addressClass);
    }

    // ================================================================
    // Source Hierarchy (Vertical) for Type
    // ================================================================

    @Nested
    @DisplayName("Source Hierarchy (Vertical)")
    class SourceHierarchy {

        /**
         * Spec §4: Default Type Settings — all defaults from built-in level.
         */
        @Test
        @DisplayName("T.1 Built-in defaults when no configuration provided")
        void builtInDefaults() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("_type", config.getTypeKey());
            assertEquals(TypeStrategy.URI, config.getStrategy());
            assertEquals(SerializationFormat.PLAIN, config.getFormat());
            assertEquals("schema", config.getSchemaKey());
            assertEquals("type", config.getNameKey());
            assertTrue(config.isInclude());
            assertNull(config.getValueWriterName());
            assertNull(config.getValueReaderName());
        }

        /**
         * Spec: Options (priority 1) overrides all lower sources for Type config.
         */
        @Test
        @DisplayName("T.2 Options overrides all lower sources")
        void optionsOverridesAllLowerSources() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeStrategy", "NAME"))
                    .moduleProperties(Map.of("typeStrategy", "CLASS"))
                    .factoryProperties(Map.of("typeStrategy", "SCHEMA_AND_TYPE"))
                    .resourceProperties(Map.of("typeStrategy", "URI"))
                    .optionsProperties(Map.of("typeStrategy", "NAME"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals(TypeStrategy.NAME, config.getStrategy(),
                    "Spec: Load/Save Options has highest priority for Type config");
        }

        /**
         * Spec: Resource (priority 2) overrides lower sources.
         */
        @Test
        @DisplayName("T.3 Resource overrides factory, module, annotation")
        void resourceOverridesLowerLevels() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeStrategy", "NAME"))
                    .moduleProperties(Map.of("typeStrategy", "CLASS"))
                    .factoryProperties(Map.of("typeStrategy", "SCHEMA_AND_TYPE"))
                    .resourceProperties(Map.of("typeStrategy", "URI"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals(TypeStrategy.URI, config.getStrategy());
        }

        /**
         * Spec: Factory (priority 3) overrides module, annotation.
         */
        @Test
        @DisplayName("T.4 Factory overrides module, annotation")
        void factoryOverridesLowerLevels() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeStrategy", "NAME"))
                    .moduleProperties(Map.of("typeStrategy", "CLASS"))
                    .factoryProperties(Map.of("typeStrategy", "SCHEMA_AND_TYPE"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals(TypeStrategy.SCHEMA_AND_TYPE, config.getStrategy());
        }

        /**
         * Spec: Module (priority 4) overrides annotation.
         */
        @Test
        @DisplayName("T.5 Module overrides annotation")
        void moduleOverridesAnnotation() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeStrategy", "NAME"))
                    .moduleProperties(Map.of("typeStrategy", "CLASS"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals(TypeStrategy.CLASS, config.getStrategy());
        }

        /**
         * Spec: Annotation (priority 5) overrides built-in default.
         */
        @Test
        @DisplayName("T.6 Annotation overrides built-in default")
        void annotationOverridesDefault() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeStrategy", "NAME"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals(TypeStrategy.NAME, config.getStrategy());
        }
    }

    // ================================================================
    // Scope Chain (Horizontal) for Type
    // ================================================================

    @Nested
    @DisplayName("Scope Chain (Horizontal)")
    class ScopeChain {

        /**
         * Spec: Class scope overrides global scope for Type.
         */
        @Test
        @DisplayName("T.7 Class scope overrides global scope")
        void classScopeOverridesGlobalScope() {
            Map<String, Object> classConfig = new HashMap<>();
            classConfig.put("typeKey", "personType");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeKey", "globalType",
                            "Person", classConfig
                    ))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("personType", config.getTypeKey(),
                    "Spec: Class scope overrides global scope for Type config");
        }

        /**
         * Spec: Different EClasses resolve different Type configs.
         */
        @Test
        @DisplayName("T.8 Different EClasses get different Type configs")
        void differentEClassesDifferentConfigs() {
            Map<String, Object> personConfig = new HashMap<>();
            personConfig.put("typeStrategy", "NAME");
            personConfig.put("typeKey", "personType");

            Map<String, Object> addressConfig = new HashMap<>();
            addressConfig.put("typeStrategy", "URI");
            addressConfig.put("typeKey", "addressType");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "Person", personConfig,
                            "Address", addressConfig
                    ))
                    .build();

            TypeConfig personTypeConfig = resolver.resolveTypeConfig(personClass, diagnostics);
            TypeConfig addressTypeConfig = resolver.resolveTypeConfig(addressClass, diagnostics);

            assertEquals(TypeStrategy.NAME, personTypeConfig.getStrategy());
            assertEquals("personType", personTypeConfig.getTypeKey());
            assertEquals(TypeStrategy.URI, addressTypeConfig.getStrategy());
            assertEquals("addressType", addressTypeConfig.getTypeKey());
        }
    }

    // ================================================================
    // Combined Resolution for Type
    // ================================================================

    @Nested
    @DisplayName("Combined Resolution")
    class CombinedResolution {

        /**
         * Spec: Different Type properties can come from different levels.
         * typeKey from options, typeStrategy from annotation, typeFormat from module.
         */
        @Test
        @DisplayName("T.9 Different properties from different sources")
        void differentPropertiesFromDifferentSources() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeStrategy", "NAME"))
                    .moduleProperties(Map.of("typeFormat", "STRUCTURED"))
                    .optionsProperties(Map.of("typeKey", "@type"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("@type", config.getTypeKey(), "from options");
            assertEquals(TypeStrategy.NAME, config.getStrategy(), "from annotation");
            assertEquals(SerializationFormat.STRUCTURED, config.getFormat(), "from module");
        }

        /**
         * Spec: Dynamic runtime overrides static model for Type.
         */
        @Test
        @DisplayName("T.10 Dynamic (runtime) overrides static (model) for Type strategy")
        void dynamicOverridesStatic() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeStrategy", "URI"))
                    .optionsProperties(Map.of("typeStrategy", "NAME"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals(TypeStrategy.NAME, config.getStrategy(),
                    "Spec: Runtime options override annotation for Type strategy");
        }
    }

    // ================================================================
    // Caching for Type
    // ================================================================

    @Nested
    @DisplayName("Caching")
    class Caching {

        /**
         * Spec §8: Configuration is resolved once, cached.
         */
        @Test
        @DisplayName("T.11 Type config is cached for same EClass")
        void typeConfigIsCached() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            TypeConfig first = resolver.resolveTypeConfig(personClass, diagnostics);
            TypeConfig second = resolver.resolveTypeConfig(personClass, diagnostics);

            assertSame(first, second,
                    "Spec §8: Type config should be resolved once and cached");
        }

        /**
         * Spec §8: clearCaches allows re-resolution.
         */
        @Test
        @DisplayName("T.12 clearCaches allows re-resolution of Type config")
        void clearCachesAllowsReResolution() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            TypeConfig first = resolver.resolveTypeConfig(personClass, diagnostics);
            resolver.clearCaches();
            TypeConfig second = resolver.resolveTypeConfig(personClass, diagnostics);

            assertNotNull(second);
            assertEquals(first.getTypeKey(), second.getTypeKey());
        }
    }

    // ================================================================
    // Validation Integration for Type
    // ================================================================

    @Nested
    @DisplayName("Validation Integration")
    class ValidationIntegration {

        /**
         * Spec §7 T-V20: typeNameKey with PLAIN format produces WARNING during resolution.
         */
        @Test
        @DisplayName("T.13 Validation called: typeNameKey with PLAIN triggers WARNING")
        void validationCalled_nameKeyPlain() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeFormat", "PLAIN",
                            "typeNameKey", "customName"
                    ))
                    .build();

            resolver.resolveTypeConfig(personClass, diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                    "Spec §7: typeNameKey with PLAIN format should produce WARNING during resolution");
        }

        /**
         * Spec §7 T-V21: typeSchemaKey with PLAIN format (non-SCHEMA_AND_TYPE) produces WARNING.
         */
        @Test
        @DisplayName("T.14 Validation called: typeSchemaKey with PLAIN + non-SCHEMA_AND_TYPE triggers WARNING")
        void validationCalled_schemaKeyPlainNonSchemaAndType() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeFormat", "PLAIN",
                            "typeStrategy", "URI",
                            "typeSchemaKey", "customSchema"
                    ))
                    .build();

            resolver.resolveTypeConfig(personClass, diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                    "Spec §7: typeSchemaKey with PLAIN + non-SCHEMA_AND_TYPE should produce WARNING");
        }

        /**
         * Spec §7: Valid config produces no diagnostics.
         */
        @Test
        @DisplayName("T.15 Valid config produces no diagnostics")
        void validConfig_noDiagnostics() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeFormat", "STRUCTURED",
                            "typeStrategy", "SCHEMA_AND_TYPE",
                            "typeSchemaKey", "schema",
                            "typeNameKey", "type"
                    ))
                    .build();

            resolver.resolveTypeConfig(personClass, diagnostics);

            assertFalse(diagnostics.hasErrors());
            assertFalse(diagnostics.hasWarnings());
        }
    }

    // ================================================================
    // Type-specific Properties through Resolver
    // ================================================================

    @Nested
    @DisplayName("Type-specific Properties through Resolver")
    class TypeSpecificProperties {

        /**
         * Spec §1: TypeStrategy.NONE can be set through resolver.
         */
        @Test
        @DisplayName("T.16 TypeStrategy.NONE propagated through resolver")
        void typeStrategyNone_propagated() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("typeStrategy", "NONE"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals(TypeStrategy.NONE, config.getStrategy());
        }

        /**
         * Spec §1.2: STRUCTURED format propagated through resolver.
         */
        @Test
        @DisplayName("T.17 STRUCTURED format propagated through resolver")
        void structuredFormat_propagated() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("typeFormat", "STRUCTURED"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
        }

        /**
         * Spec §1.6: SCHEMA_AND_TYPE strategy with all keys propagated through resolver.
         */
        @Test
        @DisplayName("T.18 SCHEMA_AND_TYPE with all keys propagated through resolver")
        void schemaAndType_allKeys_propagated() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeStrategy", "SCHEMA_AND_TYPE",
                            "typeFormat", "STRUCTURED",
                            "typeKey", "@context",
                            "typeSchemaKey", "@vocab",
                            "typeNameKey", "@type"
                    ))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals(TypeStrategy.SCHEMA_AND_TYPE, config.getStrategy());
            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
            assertEquals("@context", config.getTypeKey());
            assertEquals("@vocab", config.getSchemaKey());
            assertEquals("@type", config.getNameKey());
        }

        /**
         * Spec §5.0/§6: ValueWriter/ValueReader names propagated through resolver.
         */
        @Test
        @DisplayName("T.19 ValueWriter/ValueReader names propagated through resolver")
        void valueWriterReaderNames_propagated() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeValueWriterName", "myTypeWriter",
                            "typeValueReaderName", "myTypeReader"
                    ))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("myTypeWriter", config.getValueWriterName());
            assertEquals("myTypeReader", config.getValueReaderName());
        }

        /**
         * Spec: include flag propagated through resolver.
         */
        @Test
        @DisplayName("T.20 include flag propagated through resolver")
        void includeFlag_propagated() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("typeInclude", false))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertFalse(config.isInclude());
        }

        /**
         * Spec: All TypeStrategy values can be set through resolver.
         */
        @Test
        @DisplayName("T.21 All TypeStrategy values propagated through resolver")
        void allStrategies_propagated() {
            for (TypeStrategy strategy : TypeStrategy.values()) {
                DiagnosticCollector diag = new DiagnosticCollector();
                ConfigurationResolver resolver = ConfigurationResolver.builder()
                        .optionsProperties(Map.of("typeStrategy", strategy.name()))
                        .build();

                TypeConfig config = resolver.resolveTypeConfig(personClass, diag);

                assertEquals(strategy, config.getStrategy(),
                        "Strategy " + strategy + " should propagate through resolver");
            }
        }
    }

    // ================================================================
    // Cross-Source Type Resolution
    // ================================================================

    @Nested
    @DisplayName("Cross-Source Type Resolution")
    class CrossSourceResolution {

        /**
         * Spec: Strategy from annotation, format from module, keys from options.
         * Each property resolved independently across sources.
         */
        @Test
        @DisplayName("T.22 Strategy + format + keys from different sources")
        void strategyFormatKeys_differentSources() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeStrategy", "SCHEMA_AND_TYPE"))
                    .moduleProperties(Map.of("typeFormat", "STRUCTURED"))
                    .optionsProperties(Map.of(
                            "typeKey", "@context",
                            "typeSchemaKey", "@vocab",
                            "typeNameKey", "@type"
                    ))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals(TypeStrategy.SCHEMA_AND_TYPE, config.getStrategy(), "from annotation");
            assertEquals(SerializationFormat.STRUCTURED, config.getFormat(), "from module");
            assertEquals("@context", config.getTypeKey(), "from options");
            assertEquals("@vocab", config.getSchemaKey(), "from options");
            assertEquals("@type", config.getNameKey(), "from options");
        }

        /**
         * Spec: Class-level strategy with global-level format.
         */
        @Test
        @DisplayName("T.23 Class-level strategy with global-level format")
        void classLevelStrategy_globalLevelFormat() {
            Map<String, Object> classConfig = new HashMap<>();
            classConfig.put("typeStrategy", "NAME");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeFormat", "STRUCTURED",
                            "Person", classConfig
                    ))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals(TypeStrategy.NAME, config.getStrategy(), "from class scope");
            assertEquals(SerializationFormat.STRUCTURED, config.getFormat(), "from global scope");
        }

        /**
         * Spec: ValueWriter at class scope, strategy at global scope.
         */
        @Test
        @DisplayName("T.24 ValueWriter at class scope, strategy at global scope")
        void valueWriterClassScope_strategyGlobalScope() {
            Map<String, Object> classConfig = new HashMap<>();
            classConfig.put("typeValueWriterName", "personTypeWriter");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeStrategy", "URI",
                            "Person", classConfig
                    ))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("personTypeWriter", config.getValueWriterName(), "from class scope");
            assertEquals(TypeStrategy.URI, config.getStrategy(), "from global scope");
        }
    }
}
