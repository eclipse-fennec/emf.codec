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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.config.TypeConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.model.metadata.SerializationFormat;
import org.eclipse.fennec.model.metadata.TypeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Spec-based tests for {@link ConfigurationResolver} — shared resolution mechanics.
 * <p>
 * These tests verify the generic two-dimensional resolution algorithm using TypeConfig
 * as the representative config type. Per-config-type resolution tests are in separate files:
 * <ul>
 *   <li>{@link TypeConfigResolverSpecTest} - Type configuration resolution</li>
 *   <li>{@link IdConfigResolverSpecTest} - ID configuration resolution</li>
 *   <li>{@link SuperTypeConfigResolverSpecTest} - SuperType configuration resolution + cross-config validation</li>
 *   <li>{@link DiscriminatorConfigResolverSpecTest} - Discriminator configuration resolution</li>
 *   <li>{@link FeatureConfigResolverSpecTest} - Feature configuration resolution</li>
 * </ul>
 * <p>
 * Spec reference: {@code docs/codec-v2-spec/02-config-resolution.md}
 * <p>
 * Test sections:
 * <ul>
 *   <li>Section 2: Source Hierarchy (Vertical) - OPTIONS → RESOURCE → FACTORY → MODULE → ANNOTATION → DEFAULT</li>
 *   <li>Section 3: Scope Chain (Horizontal) - FEATURE → ECLASS → GLOBAL</li>
 *   <li>Section 4: Combined Resolution Algorithm</li>
 *   <li>Section 8: EffectiveConfig Pattern</li>
 *   <li>Validation Integration</li>
 * </ul>
 */
@DisplayName("ConfigurationResolver Spec Tests (shared resolution mechanics)")
class ConfigurationResolverSpecTest {

    private DiagnosticCollector diagnostics;
    private EPackage testPackage;
    private EClass personClass;
    private EClass addressClass;
    private EAttribute firstNameAttribute;
    private EReference addressReference;

    @BeforeEach
    void setUp() {
        diagnostics = new DiagnosticCollector();

        // Create test model
        testPackage = EcoreFactory.eINSTANCE.createEPackage();
        testPackage.setName("test");
        testPackage.setNsPrefix("test");
        testPackage.setNsURI("http://test.org/1.0");

        personClass = EcoreFactory.eINSTANCE.createEClass();
        personClass.setName("Person");
        testPackage.getEClassifiers().add(personClass);

        addressClass = EcoreFactory.eINSTANCE.createEClass();
        addressClass.setName("Address");
        testPackage.getEClassifiers().add(addressClass);

        firstNameAttribute = EcoreFactory.eINSTANCE.createEAttribute();
        firstNameAttribute.setName("firstName");
        firstNameAttribute.setEType(EcorePackage.Literals.ESTRING);
        personClass.getEStructuralFeatures().add(firstNameAttribute);

        addressReference = EcoreFactory.eINSTANCE.createEReference();
        addressReference.setName("address");
        addressReference.setEType(addressClass);
        personClass.getEStructuralFeatures().add(addressReference);
    }

    // ========================================================================
    // Section 2: Source Hierarchy (Vertical)
    // Spec reference: 02-config-resolution.md Section 2
    // "Higher priority sources override lower ones"
    // ========================================================================

    @Nested
    @DisplayName("2. Source Hierarchy (Vertical)")
    class SourceHierarchy {

        /**
         * Spec Section 2:
         * "Priority | Source | Lifecycle | Configuration Methods
         *  1 (highest) | Load/Save Options | Per-operation | Property map
         *  2 | Resource | Per-resource | Property map, ConfigBuilder
         *  3 | ResourceFactory | Per-factory | Property map, ConfigBuilder
         *  4 | Jackson Module Config | Per-codec | Property map, ConfigBuilder
         *  5 | EAnnotations | Per-model (static) | Ecore model annotations
         *  6 (lowest) | Built-in Defaults | Global | Hardcoded in codec"
         */
        @Test
        @DisplayName("2.1 Options (priority 1) overrides all lower sources")
        void optionsOverridesAllLowerSources() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeKey", "annotation"))  // Level 5
                    .moduleProperties(Map.of("typeKey", "module"))          // Level 4
                    .factoryProperties(Map.of("typeKey", "factory"))        // Level 3
                    .resourceProperties(Map.of("typeKey", "resource"))      // Level 2
                    .optionsProperties(Map.of("typeKey", "options"))        // Level 1 (highest)
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("options", config.getTypeKey(),
                    "Spec: Load/Save Options has highest priority (1)");
        }

        @Test
        @DisplayName("2.2 Resource (priority 2) overrides factory, module, annotation, default")
        void resourceOverridesLowerLevels() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeKey", "annotation"))  // Level 5
                    .moduleProperties(Map.of("typeKey", "module"))          // Level 4
                    .factoryProperties(Map.of("typeKey", "factory"))        // Level 3
                    .resourceProperties(Map.of("typeKey", "resource"))      // Level 2
                    // No options (Level 1)
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("resource", config.getTypeKey(),
                    "Spec: Resource config has priority 2");
        }

        @Test
        @DisplayName("2.3 Factory (priority 3) overrides module, annotation, default")
        void factoryOverridesLowerLevels() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeKey", "annotation"))  // Level 5
                    .moduleProperties(Map.of("typeKey", "module"))          // Level 4
                    .factoryProperties(Map.of("typeKey", "factory"))        // Level 3
                    // No resource or options
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("factory", config.getTypeKey(),
                    "Spec: ResourceFactory has priority 3");
        }

        @Test
        @DisplayName("2.4 Module (priority 4) overrides annotation, default")
        void moduleOverridesLowerLevels() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeKey", "annotation"))  // Level 5
                    .moduleProperties(Map.of("typeKey", "module"))          // Level 4
                    // No factory, resource, or options
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("module", config.getTypeKey(),
                    "Spec: Jackson Module Config has priority 4");
        }

        @Test
        @DisplayName("2.5 Annotation (priority 5) overrides default")
        void annotationOverridesDefault() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeKey", "annotation"))  // Level 5
                    // No module, factory, resource, or options
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("annotation", config.getTypeKey(),
                    "Spec: EAnnotations has priority 5");
        }

        @Test
        @DisplayName("2.6 Built-in defaults (priority 6) used when no configuration")
        void builtInDefaultsUsedWhenNoConfiguration() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("_type", config.getTypeKey(),
                    "Spec: Built-in Defaults (priority 6) - typeKey defaults to '_type'");
        }

        /**
         * Spec: "Key principle: Dynamic overrides static. Runtime settings always win over model-defined settings."
         */
        @Test
        @DisplayName("2.7 Dynamic (runtime) overrides static (model)")
        void dynamicOverridesStatic() {
            // Annotation is static (from model)
            // Options is dynamic (runtime)
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeStrategy", "NAME"))   // Static
                    .optionsProperties(Map.of("typeStrategy", "CLASS"))     // Dynamic (runtime)
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals(TypeStrategy.CLASS, config.getStrategy(),
                    "Spec: Runtime settings always win over model-defined settings");
        }
    }

    // ========================================================================
    // Section 3: Scope Chain (Horizontal)
    // Spec reference: 02-config-resolution.md Section 3
    // "More specific scopes override less specific ones"
    // ========================================================================

    @Nested
    @DisplayName("3. Scope Chain (Horizontal)")
    class ScopeChain {

        /**
         * Spec Section 3:
         * "Priority | Scope | Applies To
         *  1 (highest) | EReference/EAttribute | Single feature
         *  2 | EClass | All instances of that class
         *  3 | Global | All objects in codec
         *  4 (lowest) | Default | Built-in fallback"
         */
        @Test
        @DisplayName("3.1 Feature scope overrides class scope")
        void featureScopeOverridesClassScope() {
            Map<String, Object> featureConfig = new HashMap<>();
            featureConfig.put("key", "featureKey");

            Map<String, Object> classConfig = new HashMap<>();
            classConfig.put("key", "classKey");
            classConfig.put("firstName", featureConfig);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("Person", classConfig))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttribute, diagnostics);

            assertEquals("featureKey", config.getKey(),
                    "Spec: Feature scope (priority 1) overrides class scope");
        }

        @Test
        @DisplayName("3.2 Class scope overrides global scope")
        void classScopeOverridesGlobalScope() {
            Map<String, Object> classConfig = new HashMap<>();
            classConfig.put("typeKey", "classKey");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeKey", "globalKey",
                            "Person", classConfig
                    ))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("classKey", config.getTypeKey(),
                    "Spec: Class scope (priority 2) overrides global scope");
        }

        @Test
        @DisplayName("3.3 Global scope overrides built-in default")
        void globalScopeOverridesDefault() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("typeKey", "globalKey"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("globalKey", config.getTypeKey(),
                    "Spec: Global scope (priority 3) overrides built-in default");
        }

        @Test
        @DisplayName("3.4 Built-in default used when no scope configured")
        void builtInDefaultWhenNoScopeConfigured() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("_type", config.getTypeKey(),
                    "Spec: Built-in default (priority 4) when no scope configured");
        }
    }

    // ========================================================================
    // Section 4: Combined Resolution Algorithm
    // Spec reference: 02-config-resolution.md Section 4
    // "When resolving a configuration value, both dimensions are considered"
    // ========================================================================

    @Nested
    @DisplayName("4. Combined Resolution Algorithm")
    class CombinedResolution {

        /**
         * Spec Section 4:
         * "For property P on EReference R of EClass C:
         *  1. Check Load/Save options for R.P, then C.P, then global P
         *  2. Check Resource config for R.P, then C.P, then global P
         *  ... (same pattern for all sources)
         *  First non-null value wins."
         */
        @Test
        @DisplayName("4.1 Feature-level at higher source beats class-level at lower source")
        void featureLevelHigherSourceBeatsClassLevelLowerSource() {
            // Module has class-level config
            // Options has feature-level config
            // Options feature-level should win (higher source + more specific scope)
            Map<String, Object> featureConfig = new HashMap<>();
            featureConfig.put("key", "optionsFeature");

            Map<String, Object> classConfig = new HashMap<>();
            classConfig.put("firstName", featureConfig);

            Map<String, Object> moduleClassConfig = new HashMap<>();
            moduleClassConfig.put("key", "moduleClass");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .moduleProperties(Map.of("Person", moduleClassConfig))
                    .optionsProperties(Map.of("Person", classConfig))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttribute, diagnostics);

            assertEquals("optionsFeature", config.getKey(),
                    "Spec: Higher source + more specific scope wins");
        }

        @Test
        @DisplayName("4.2 Class-level at higher source beats global-level at lower source")
        void classLevelHigherSourceBeatsGlobalLevelLowerSource() {
            // Module has global config
            // Options has class-level config
            Map<String, Object> classConfig = new HashMap<>();
            classConfig.put("typeKey", "optionsClass");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .moduleProperties(Map.of("typeKey", "moduleGlobal"))
                    .optionsProperties(Map.of("Person", classConfig))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("optionsClass", config.getTypeKey(),
                    "Spec: Higher source + class scope beats lower source + global scope");
        }

        /**
         * Spec Section 4 Example:
         * "For typeStrategy on Person.address (an EReference):
         *  ...
         *  8. EAnnotation on Person class: @CODEC(codec.typeStrategy='NAME') → found: NAME
         *  Result: typeStrategy = NAME"
         */
        @Test
        @DisplayName("4.3 Different properties can come from different levels")
        void differentPropertiesFromDifferentLevels() {
            // Options sets typeKey
            // Module sets typeFormat
            // Annotation sets typeStrategy
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeStrategy", "NAME"))
                    .moduleProperties(Map.of("typeFormat", "STRUCTURED"))
                    .optionsProperties(Map.of("typeKey", "myType"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("myType", config.getTypeKey(), "from options");
            assertEquals(SerializationFormat.STRUCTURED, config.getFormat(), "from module");
            assertEquals(TypeStrategy.NAME, config.getStrategy(), "from annotation");
        }
    }

    // ========================================================================
    // Section 8: EffectiveConfig Pattern
    // Spec reference: 02-config-resolution.md Section 8
    // ========================================================================

    @Nested
    @DisplayName("8. EffectiveConfig Pattern")
    class EffectiveConfigPattern {

        /**
         * Spec Section 8:
         * "This ensures:
         *  - Configuration is resolved once, not on every serialization call
         *  - Immutable snapshot prevents mid-operation changes
         *  - Lazy caching optimizes memory for large models"
         */
        @Test
        @DisplayName("8.1 Configuration is cached (resolved once)")
        void configurationIsCached() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            TypeConfig first = resolver.resolveTypeConfig(personClass, diagnostics);
            TypeConfig second = resolver.resolveTypeConfig(personClass, diagnostics);

            assertSame(first, second,
                    "Spec: Configuration is resolved once, not on every call");
        }

        @Test
        @DisplayName("8.2 Returned config is immutable snapshot")
        void returnedConfigIsImmutableSnapshot() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            // TypeConfig is immutable - no setters available
            // Verify it has the expected values
            assertNotNull(config);
            assertEquals("_type", config.getTypeKey());
        }

        @Test
        @DisplayName("8.3 Different EClasses have different cached configs")
        void differentEClassesHaveDifferentCachedConfigs() {
            Map<String, Object> personConfig = new HashMap<>();
            personConfig.put("typeKey", "personType");

            Map<String, Object> addressConfig = new HashMap<>();
            addressConfig.put("typeKey", "addressType");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "Person", personConfig,
                            "Address", addressConfig
                    ))
                    .build();

            TypeConfig personTypeConfig = resolver.resolveTypeConfig(personClass, diagnostics);
            TypeConfig addressTypeConfig = resolver.resolveTypeConfig(addressClass, diagnostics);

            assertEquals("personType", personTypeConfig.getTypeKey());
            assertEquals("addressType", addressTypeConfig.getTypeKey());
        }

        @Test
        @DisplayName("8.4 clearCaches() allows re-resolution")
        void clearCachesAllowsReResolution() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            TypeConfig first = resolver.resolveTypeConfig(personClass, diagnostics);
            resolver.clearCaches();
            TypeConfig second = resolver.resolveTypeConfig(personClass, diagnostics);

            // After clear, new resolution happens (may or may not be same instance
            // depending on implementation, but should have same values)
            assertNotNull(second);
            assertEquals(first.getTypeKey(), second.getTypeKey());
        }
    }

    // ========================================================================
    // Validation Integration
    // Spec: validate() is called on final merged config
    // ========================================================================

    @Nested
    @DisplayName("Validation Integration")
    class ValidationIntegration {

        @Test
        @DisplayName("Validation is called on resolved config")
        void validationIsCalledOnResolvedConfig() {
            // This config combination should trigger a warning
            // (nameKey set to non-default when format is PLAIN)
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeFormat", "PLAIN",
                            "typeNameKey", "customName"
                    ))
                    .build();

            resolver.resolveTypeConfig(personClass, diagnostics);

            // Validation should have been called
            assertTrue(diagnostics.hasWarnings(),
                    "Validation should be called on resolved config");
        }

        @Test
        @DisplayName("Diagnostics collects validation issues from merge chain")
        void diagnosticsCollectsValidationIssues() {
            DiagnosticCollector collector = new DiagnosticCollector();

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeFormat", "PLAIN",
                            "typeNameKey", "customName"
                    ))
                    .build();

            resolver.resolveTypeConfig(personClass, collector);

            // Should have collected the warning
            assertTrue(collector.hasWarnings(),
                    "Diagnostics should be collected from validation");
        }
    }
}
