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
import org.eclipse.fennec.codec.config.SuperTypeConfig;
import org.eclipse.fennec.codec.config.TypeConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.model.metadata.SerializationFormat;
import org.eclipse.fennec.model.metadata.SuperTypeSelection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Spec-based resolver tests for SuperType configuration.
 * <p>
 * Verifies that {@link ConfigurationResolver#resolveSuperTypeConfig} applies the
 * two-dimensional resolution (Source Hierarchy × Scope Chain) correctly,
 * and that cross-config validation (Type × SuperType) works.
 * <p>
 * Spec references:
 * <ul>
 *   <li>{@code docs/codec-v2-spec/02-config-resolution.md} - Configuration Resolution</li>
 *   <li>{@code docs/codec-v2-spec/07-supertype.md} - SuperType Serialization (§6.0, §6.1)</li>
 * </ul>
 */
@DisplayName("SuperType Configuration Resolver Spec Tests (spec 07-supertype.md §6.1)")
class SuperTypeConfigResolverSpecTest {

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
    // SuperType Resolution
    // ================================================================

    @Nested
    @DisplayName("SuperType Resolution")
    class SuperTypeResolution {

        // ----------------------------------------------------------------
        // Source Hierarchy (Vertical) for SuperType
        // ----------------------------------------------------------------

        /**
         * Spec §6.1: Default SuperType Settings — all defaults from built-in level.
         */
        @Test
        @DisplayName("ST.1 Built-in defaults when no configuration provided")
        void builtInDefaults() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            SuperTypeConfig config = resolver.resolveSuperTypeConfig(personClass, diagnostics);

            assertFalse(config.isSerialize());
            assertEquals(SuperTypeSelection.ALL, config.getStrategy());
            assertTrue(config.isAsArray());
            assertEquals(",", config.getSeparator());
            assertNull(config.getFormat());
            assertNull(config.getSuperTypeKey());
            assertNull(config.getValueWriterName());
            assertNull(config.getValueReaderName());
        }

        /**
         * Spec: Options (priority 1) overrides all lower sources for SuperType config.
         */
        @Test
        @DisplayName("ST.2 Options overrides all lower sources")
        void optionsOverridesAllLowerSources() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("superTypeStrategy", "ALL"))
                    .moduleProperties(Map.of("superTypeStrategy", "ALL_EMF"))
                    .factoryProperties(Map.of("superTypeStrategy", "SINGLE"))
                    .resourceProperties(Map.of("superTypeStrategy", "NONE"))
                    .optionsProperties(Map.of("superTypeStrategy", "ALL"))
                    .build();

            SuperTypeConfig config = resolver.resolveSuperTypeConfig(personClass, diagnostics);

            assertEquals(SuperTypeSelection.ALL, config.getStrategy(),
                    "Spec: Load/Save Options has highest priority for SuperType config");
        }

        /**
         * Spec: Resource (priority 2) overrides lower sources.
         */
        @Test
        @DisplayName("ST.3 Resource overrides factory, module, annotation")
        void resourceOverridesLowerLevels() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("superTypeStrategy", "ALL"))
                    .moduleProperties(Map.of("superTypeStrategy", "ALL_EMF"))
                    .factoryProperties(Map.of("superTypeStrategy", "SINGLE"))
                    .resourceProperties(Map.of("superTypeStrategy", "NONE"))
                    .build();

            SuperTypeConfig config = resolver.resolveSuperTypeConfig(personClass, diagnostics);

            assertEquals(SuperTypeSelection.NONE, config.getStrategy());
        }

        /**
         * Spec: Factory (priority 3) overrides module, annotation.
         */
        @Test
        @DisplayName("ST.4 Factory overrides module, annotation")
        void factoryOverridesLowerLevels() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("superTypeStrategy", "ALL"))
                    .moduleProperties(Map.of("superTypeStrategy", "ALL_EMF"))
                    .factoryProperties(Map.of("superTypeStrategy", "SINGLE"))
                    .build();

            SuperTypeConfig config = resolver.resolveSuperTypeConfig(personClass, diagnostics);

            assertEquals(SuperTypeSelection.SINGLE, config.getStrategy());
        }

        /**
         * Spec: Module (priority 4) overrides annotation.
         */
        @Test
        @DisplayName("ST.5 Module overrides annotation")
        void moduleOverridesAnnotation() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("superTypeStrategy", "ALL"))
                    .moduleProperties(Map.of("superTypeStrategy", "ALL_EMF"))
                    .build();

            SuperTypeConfig config = resolver.resolveSuperTypeConfig(personClass, diagnostics);

            assertEquals(SuperTypeSelection.ALL_EMF, config.getStrategy());
        }

        /**
         * Spec: Annotation (priority 5) overrides built-in default.
         */
        @Test
        @DisplayName("ST.6 Annotation overrides built-in default")
        void annotationOverridesDefault() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("superTypeStrategy", "SINGLE"))
                    .build();

            SuperTypeConfig config = resolver.resolveSuperTypeConfig(personClass, diagnostics);

            assertEquals(SuperTypeSelection.SINGLE, config.getStrategy());
        }

        // ----------------------------------------------------------------
        // Scope Chain (Horizontal) for SuperType
        // ----------------------------------------------------------------

        /**
         * Spec: Class scope overrides global scope for SuperType.
         */
        @Test
        @DisplayName("ST.7 Class scope overrides global scope")
        void classScopeOverridesGlobalScope() {
            Map<String, Object> classConfig = new HashMap<>();
            classConfig.put("superTypeSerialize", true);
            classConfig.put("superTypeStrategy", "SINGLE");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "superTypeSerialize", true,
                            "superTypeStrategy", "ALL",
                            "Person", classConfig
                    ))
                    .build();

            SuperTypeConfig config = resolver.resolveSuperTypeConfig(personClass, diagnostics);

            assertEquals(SuperTypeSelection.SINGLE, config.getStrategy(),
                    "Spec: Class scope overrides global scope for SuperType config");
        }

        /**
         * Spec: Different EClasses resolve different SuperType configs.
         */
        @Test
        @DisplayName("ST.8 Different EClasses get different SuperType configs")
        void differentEClassesDifferentConfigs() {
            Map<String, Object> personConfig = new HashMap<>();
            personConfig.put("superTypeSerialize", true);
            personConfig.put("superTypeStrategy", "ALL");

            Map<String, Object> addressConfig = new HashMap<>();
            addressConfig.put("superTypeSerialize", false);
            addressConfig.put("superTypeStrategy", "NONE");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "Person", personConfig,
                            "Address", addressConfig
                    ))
                    .build();

            SuperTypeConfig personSuperTypeConfig = resolver.resolveSuperTypeConfig(personClass, diagnostics);
            SuperTypeConfig addressSuperTypeConfig = resolver.resolveSuperTypeConfig(addressClass, diagnostics);

            assertTrue(personSuperTypeConfig.isSerialize());
            assertEquals(SuperTypeSelection.ALL, personSuperTypeConfig.getStrategy());
            assertFalse(addressSuperTypeConfig.isSerialize());
            assertEquals(SuperTypeSelection.NONE, addressSuperTypeConfig.getStrategy());
        }

        // ----------------------------------------------------------------
        // Combined Resolution for SuperType
        // ----------------------------------------------------------------

        /**
         * Spec: Different SuperType properties can come from different levels.
         */
        @Test
        @DisplayName("ST.9 Different properties from different sources")
        void differentPropertiesFromDifferentSources() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("superTypeSerialize", true))
                    .moduleProperties(Map.of("superTypeSeparator", "|"))
                    .optionsProperties(Map.of("superTypeStrategy", "SINGLE"))
                    .build();

            SuperTypeConfig config = resolver.resolveSuperTypeConfig(personClass, diagnostics);

            assertTrue(config.isSerialize(), "from annotation");
            assertEquals("|", config.getSeparator(), "from module");
            assertEquals(SuperTypeSelection.SINGLE, config.getStrategy(), "from options");
        }

        /**
         * Spec: Dynamic runtime overrides static model for SuperType.
         */
        @Test
        @DisplayName("ST.10 Dynamic (runtime) overrides static (model) for SuperType strategy")
        void dynamicOverridesStatic() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("superTypeStrategy", "ALL"))
                    .optionsProperties(Map.of("superTypeStrategy", "SINGLE"))
                    .build();

            SuperTypeConfig config = resolver.resolveSuperTypeConfig(personClass, diagnostics);

            assertEquals(SuperTypeSelection.SINGLE, config.getStrategy(),
                    "Spec: Runtime options override annotation for SuperType strategy");
        }

        // ----------------------------------------------------------------
        // Caching for SuperType
        // ----------------------------------------------------------------

        /**
         * Spec §8: Configuration is resolved once, cached.
         */
        @Test
        @DisplayName("ST.11 SuperType config is cached for same EClass")
        void superTypeConfigIsCached() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            SuperTypeConfig first = resolver.resolveSuperTypeConfig(personClass, diagnostics);
            SuperTypeConfig second = resolver.resolveSuperTypeConfig(personClass, diagnostics);

            assertSame(first, second,
                    "Spec §8: SuperType config should be resolved once and cached");
        }

        /**
         * Spec §8: clearCaches allows re-resolution.
         */
        @Test
        @DisplayName("ST.12 clearCaches allows re-resolution of SuperType config")
        void clearCachesAllowsReResolution() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            SuperTypeConfig first = resolver.resolveSuperTypeConfig(personClass, diagnostics);
            resolver.clearCaches();
            SuperTypeConfig second = resolver.resolveSuperTypeConfig(personClass, diagnostics);

            assertNotNull(second);
            assertEquals(first.getStrategy(), second.getStrategy());
        }

        // ----------------------------------------------------------------
        // Validation Integration for SuperType
        // ----------------------------------------------------------------

        /**
         * Spec §6.0: Custom separator with asArray=true produces WARNING during resolution.
         */
        @Test
        @DisplayName("ST.13 Validation called: custom separator with asArray=true triggers WARNING")
        void validationCalled_customSeparatorAsArrayTrue() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "superTypeAsArray", true,
                            "superTypeSeparator", "|"
                    ))
                    .build();

            resolver.resolveSuperTypeConfig(personClass, diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                    "Spec: Custom separator with asArray=true should produce WARNING during resolution");
        }

        /**
         * Spec §6.0: Valid config produces no diagnostics.
         */
        @Test
        @DisplayName("ST.14 Valid config produces no diagnostics")
        void validConfig_noDiagnostics() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "superTypeSerialize", true,
                            "superTypeStrategy", "ALL",
                            "superTypeAsArray", true
                    ))
                    .build();

            resolver.resolveSuperTypeConfig(personClass, diagnostics);

            assertFalse(diagnostics.hasErrors());
            assertFalse(diagnostics.hasWarnings());
        }

        // ----------------------------------------------------------------
        // SuperType-specific properties through resolver
        // ----------------------------------------------------------------

        /**
         * Spec §6.1: superTypeAsArray can be set through resolver.
         */
        @Test
        @DisplayName("ST.15 superTypeAsArray propagated through resolver")
        void superTypeAsArray_propagated() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("superTypeAsArray", false))
                    .build();

            SuperTypeConfig config = resolver.resolveSuperTypeConfig(personClass, diagnostics);

            assertFalse(config.isAsArray());
        }

        /**
         * Spec §6.1: superTypeFormat can be set through resolver.
         */
        @Test
        @DisplayName("ST.16 superTypeFormat propagated through resolver")
        void superTypeFormat_propagated() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("superTypeFormat", "STRUCTURED"))
                    .build();

            SuperTypeConfig config = resolver.resolveSuperTypeConfig(personClass, diagnostics);

            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
        }

        /**
         * Spec §6.1: ValueWriter/ValueReader names propagated through resolver.
         */
        @Test
        @DisplayName("ST.17 ValueWriter/ValueReader names propagated through resolver")
        void valueWriterReaderNames_propagated() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "superTypeValueWriterName", "mySuperTypeWriter",
                            "superTypeValueReaderName", "mySuperTypeReader"
                    ))
                    .build();

            SuperTypeConfig config = resolver.resolveSuperTypeConfig(personClass, diagnostics);

            assertEquals("mySuperTypeWriter", config.getValueWriterName());
            assertEquals("mySuperTypeReader", config.getValueReaderName());
        }
    }

    // ================================================================
    // Cross-Config Validation (Type × SuperType)
    // Spec reference: 07-supertype.md Section 6.0
    // ================================================================

    @Nested
    @DisplayName("Cross-Config Validation (spec 07-supertype.md §6.0)")
    class CrossConfigValidation {

        /**
         * Spec 07-supertype.md §6.0:
         * "STRUCTURED requires Type: typeFormat=STRUCTURED + typeStrategy=NONE + superTypeSerialize=true → ERROR"
         */
        @Test
        @DisplayName("6.0.1 STRUCTURED + NONE + superTypeSerialize=true is ERROR")
        void structuredRequiresType() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeFormat", "STRUCTURED",
                            "typeStrategy", "NONE",
                            "superTypeSerialize", true
                    ))
                    .build();

            TypeConfig typeConfig = resolver.resolveTypeConfig(personClass, diagnostics);
            SuperTypeConfig superTypeConfig = resolver.resolveSuperTypeConfig(personClass, diagnostics);
            resolver.validateCrossConfig(typeConfig, superTypeConfig, diagnostics);

            assertTrue(diagnostics.hasErrors(),
                    "Spec §6.0: STRUCTURED + NONE + superTypeSerialize=true must produce ERROR");
        }

        /**
         * Spec 07-supertype.md §6.0:
         * "Custom reader conflict: STRUCTURED + typeValueReaderName + superTypeValueReaderName → WARNING"
         */
        @Test
        @DisplayName("6.0.2 Custom reader conflict in STRUCTURED produces WARNING")
        void customReaderConflict() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeFormat", "STRUCTURED",
                            "typeValueReaderName", "typeReader",
                            "superTypeValueReaderName", "superTypeReader"
                    ))
                    .build();

            TypeConfig typeConfig = resolver.resolveTypeConfig(personClass, diagnostics);
            SuperTypeConfig superTypeConfig = resolver.resolveSuperTypeConfig(personClass, diagnostics);
            resolver.validateCrossConfig(typeConfig, superTypeConfig, diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                    "Spec §6.0: Custom reader conflict in STRUCTURED must produce WARNING");
        }

        /**
         * Spec 07-supertype.md §6.0:
         * "Custom writer conflict: STRUCTURED + typeValueWriterName + superTypeValueWriterName → WARNING"
         */
        @Test
        @DisplayName("6.0.3 Custom writer conflict in STRUCTURED produces WARNING")
        void customWriterConflict() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeFormat", "STRUCTURED",
                            "typeValueWriterName", "typeWriter",
                            "superTypeValueWriterName", "superTypeWriter"
                    ))
                    .build();

            TypeConfig typeConfig = resolver.resolveTypeConfig(personClass, diagnostics);
            SuperTypeConfig superTypeConfig = resolver.resolveSuperTypeConfig(personClass, diagnostics);
            resolver.validateCrossConfig(typeConfig, superTypeConfig, diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                    "Spec §6.0: Custom writer conflict in STRUCTURED must produce WARNING");
        }

        /**
         * Spec 07-supertype.md §6.0:
         * "Format follows Type: superTypeFormat not explicitly set → Inherits from typeFormat"
         */
        @Test
        @DisplayName("6.0.4 Inherited format triggers STRUCTURED constraint")
        void inheritedFormatTriggersConstraint() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeFormat", "STRUCTURED",
                            "typeStrategy", "NONE",
                            "superTypeSerialize", true
                    ))
                    .build();

            TypeConfig typeConfig = resolver.resolveTypeConfig(personClass, diagnostics);
            SuperTypeConfig superTypeConfig = resolver.resolveSuperTypeConfig(personClass, diagnostics);
            resolver.validateCrossConfig(typeConfig, superTypeConfig, diagnostics);

            assertTrue(diagnostics.hasErrors(),
                    "Spec §6.0: Inherited STRUCTURED format must trigger constraint");
        }
    }
}
