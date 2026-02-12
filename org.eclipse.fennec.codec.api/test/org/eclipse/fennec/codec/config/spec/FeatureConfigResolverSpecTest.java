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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.model.metadata.EnumSerializationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Spec-based resolver tests for Feature configuration.
 * <p>
 * Verifies that {@link ConfigurationResolver#resolveFeatureConfig} applies the
 * two-dimensional resolution (Source Hierarchy × Scope Chain) correctly.
 * Feature config has a 3-level scope chain: Global → EClass → Feature.
 * <p>
 * Spec references:
 * <ul>
 *   <li>{@code docs/codec-v2-spec/02-config-resolution.md} - Configuration Resolution</li>
 *   <li>{@code docs/codec-v2-spec/11-feature.md} - Feature Serialization</li>
 * </ul>
 */
@DisplayName("Feature Configuration Resolver Spec Tests (spec 11-feature.md)")
class FeatureConfigResolverSpecTest {

    private DiagnosticCollector diagnostics;
    private EPackage testPackage;
    private EClass personClass;
    private EClass addressClass;
    private EAttribute firstNameAttr;
    private EAttribute lastNameAttr;
    private EAttribute streetAttr;

    @BeforeEach
    void setUp() {
        diagnostics = new DiagnosticCollector();

        testPackage = EcoreFactory.eINSTANCE.createEPackage();
        testPackage.setName("test");
        testPackage.setNsPrefix("test");
        testPackage.setNsURI("http://test.org/1.0");

        personClass = EcoreFactory.eINSTANCE.createEClass();
        personClass.setName("Person");
        testPackage.getEClassifiers().add(personClass);

        firstNameAttr = EcoreFactory.eINSTANCE.createEAttribute();
        firstNameAttr.setName("firstName");
        firstNameAttr.setEType(EcorePackage.Literals.ESTRING);
        personClass.getEStructuralFeatures().add(firstNameAttr);

        lastNameAttr = EcoreFactory.eINSTANCE.createEAttribute();
        lastNameAttr.setName("lastName");
        lastNameAttr.setEType(EcorePackage.Literals.ESTRING);
        personClass.getEStructuralFeatures().add(lastNameAttr);

        addressClass = EcoreFactory.eINSTANCE.createEClass();
        addressClass.setName("Address");
        testPackage.getEClassifiers().add(addressClass);

        streetAttr = EcoreFactory.eINSTANCE.createEAttribute();
        streetAttr.setName("street");
        streetAttr.setEType(EcorePackage.Literals.ESTRING);
        addressClass.getEStructuralFeatures().add(streetAttr);
    }

    // ================================================================
    // Source Hierarchy (Vertical) for Feature
    // ================================================================

    @Nested
    @DisplayName("Source Hierarchy (Vertical)")
    class SourceHierarchy {

        /**
         * Spec: Default Feature Settings — all defaults from built-in level.
         */
        @Test
        @DisplayName("F.1 Built-in defaults when no configuration provided")
        void builtInDefaults() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            // After resolution, null key is resolved to feature name (spec: null = "use feature name")
            assertEquals("firstName", config.getKey());
            assertFalse(config.isIgnore());
            assertFalse(config.isIgnoreRead());
            assertFalse(config.isIgnoreWrite());
            assertFalse(config.isForceRead());
            assertFalse(config.isForceWrite());
            assertFalse(config.isSerializeNull());
            assertFalse(config.isSerializeEmpty());
            assertFalse(config.isSerializeDefault());
            assertEquals(EnumSerializationStrategy.LITERAL, config.getEnumSerialization());
            assertNull(config.getValueWriterName());
            assertNull(config.getValueReaderName());
        }

        /**
         * Spec: Options (priority 1) overrides all lower sources for Feature config.
         */
        @Test
        @DisplayName("F.2 Options overrides all lower sources")
        void optionsOverridesAllLowerSources() {
            Map<String, Object> featureProps = Map.of("ignore", true);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(featureConfig("Person", "firstName", Map.of("ignore", false)))
                    .moduleProperties(featureConfig("Person", "firstName", Map.of("ignore", false)))
                    .factoryProperties(featureConfig("Person", "firstName", Map.of("ignore", false)))
                    .resourceProperties(featureConfig("Person", "firstName", Map.of("ignore", false)))
                    .optionsProperties(featureConfig("Person", "firstName", featureProps))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertTrue(config.isIgnore(),
                    "Spec: Load/Save Options has highest priority for Feature config");
        }

        /**
         * Spec: Resource (priority 2) overrides lower sources.
         */
        @Test
        @DisplayName("F.3 Resource overrides factory, module, annotation")
        void resourceOverridesLowerLevels() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(featureConfig("Person", "firstName", Map.of("key", "annotation")))
                    .moduleProperties(featureConfig("Person", "firstName", Map.of("key", "module")))
                    .factoryProperties(featureConfig("Person", "firstName", Map.of("key", "factory")))
                    .resourceProperties(featureConfig("Person", "firstName", Map.of("key", "resource")))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertEquals("resource", config.getKey());
        }

        /**
         * Spec: Factory (priority 3) overrides module, annotation.
         */
        @Test
        @DisplayName("F.4 Factory overrides module, annotation")
        void factoryOverridesLowerLevels() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(featureConfig("Person", "firstName", Map.of("key", "annotation")))
                    .moduleProperties(featureConfig("Person", "firstName", Map.of("key", "module")))
                    .factoryProperties(featureConfig("Person", "firstName", Map.of("key", "factory")))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertEquals("factory", config.getKey());
        }

        /**
         * Spec: Module (priority 4) overrides annotation.
         */
        @Test
        @DisplayName("F.5 Module overrides annotation")
        void moduleOverridesAnnotation() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(featureConfig("Person", "firstName", Map.of("key", "annotation")))
                    .moduleProperties(featureConfig("Person", "firstName", Map.of("key", "module")))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertEquals("module", config.getKey());
        }

        /**
         * Spec: Annotation (priority 5) overrides built-in default.
         */
        @Test
        @DisplayName("F.6 Annotation overrides built-in default")
        void annotationOverridesDefault() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(featureConfig("Person", "firstName", Map.of("key", "first_name")))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertEquals("first_name", config.getKey());
        }
    }

    // ================================================================
    // Scope Chain (Horizontal) for Feature — 3 levels
    // ================================================================

    @Nested
    @DisplayName("Scope Chain (Horizontal) — Global → Class → Feature")
    class ScopeChain {

        /**
         * Spec: Feature scope overrides class scope for Feature config.
         */
        @Test
        @DisplayName("F.7 Feature scope overrides class scope")
        void featureScopeOverridesClassScope() {
            Map<String, Object> classProps = new HashMap<>();
            classProps.put("ignore", true);

            Map<String, Object> featureProps = new HashMap<>();
            featureProps.put("ignore", false);
            classProps.put("firstName", featureProps);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("Person", classProps))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertFalse(config.isIgnore(),
                    "Spec: Feature scope overrides class scope");
        }

        /**
         * Spec: Class scope overrides global scope for Feature config.
         */
        @Test
        @DisplayName("F.8 Class scope overrides global scope")
        void classScopeOverridesGlobalScope() {
            Map<String, Object> classProps = new HashMap<>();
            classProps.put("serializeNull", true);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "serializeNull", false,
                            "Person", classProps
                    ))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertTrue(config.isSerializeNull(),
                    "Spec: Class scope overrides global scope");
        }

        /**
         * Spec: Feature scope overrides global scope (skip class).
         */
        @Test
        @DisplayName("F.9 Feature scope overrides global scope (skipping class)")
        void featureScopeOverridesGlobalScope() {
            Map<String, Object> featureProps = new HashMap<>();
            featureProps.put("key", "given_name");

            Map<String, Object> classProps = new HashMap<>();
            classProps.put("firstName", featureProps);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "key", "globalKey",
                            "Person", classProps
                    ))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertEquals("given_name", config.getKey(),
                    "Spec: Feature scope overrides global scope");
        }

        /**
         * Spec: Global scope applies when no class/feature scope configured.
         */
        @Test
        @DisplayName("F.10 Global scope applies as fallback")
        void globalScopeAppliesAsFallback() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("serializeNull", true))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertTrue(config.isSerializeNull(),
                    "Spec: Global scope applies when no class/feature scope");
        }

        /**
         * Spec: Different features on same class can have different configs.
         */
        @Test
        @DisplayName("F.11 Different features on same class get different configs")
        void differentFeaturesOnSameClass() {
            Map<String, Object> firstNameProps = new HashMap<>();
            firstNameProps.put("key", "given_name");
            firstNameProps.put("ignore", false);

            Map<String, Object> lastNameProps = new HashMap<>();
            lastNameProps.put("key", "family_name");
            lastNameProps.put("ignore", true);

            Map<String, Object> classProps = new HashMap<>();
            classProps.put("firstName", firstNameProps);
            classProps.put("lastName", lastNameProps);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("Person", classProps))
                    .build();

            FeatureConfig firstNameConfig = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);
            FeatureConfig lastNameConfig = resolver.resolveFeatureConfig(lastNameAttr, diagnostics);

            assertEquals("given_name", firstNameConfig.getKey());
            assertFalse(firstNameConfig.isIgnore());
            assertEquals("family_name", lastNameConfig.getKey());
            assertTrue(lastNameConfig.isIgnore());
        }

        /**
         * Spec: Features on different classes resolve independently.
         */
        @Test
        @DisplayName("F.12 Features on different classes resolve independently")
        void featuresOnDifferentClasses() {
            Map<String, Object> firstNameProps = new HashMap<>();
            firstNameProps.put("key", "given_name");

            Map<String, Object> personProps = new HashMap<>();
            personProps.put("firstName", firstNameProps);

            Map<String, Object> streetProps = new HashMap<>();
            streetProps.put("key", "street_name");

            Map<String, Object> addressProps = new HashMap<>();
            addressProps.put("street", streetProps);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "Person", personProps,
                            "Address", addressProps
                    ))
                    .build();

            FeatureConfig firstNameConfig = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);
            FeatureConfig streetConfig = resolver.resolveFeatureConfig(streetAttr, diagnostics);

            assertEquals("given_name", firstNameConfig.getKey());
            assertEquals("street_name", streetConfig.getKey());
        }
    }

    // ================================================================
    // Combined Resolution
    // ================================================================

    @Nested
    @DisplayName("Combined Resolution")
    class CombinedResolution {

        /**
         * Spec: Different feature properties can come from different levels.
         * key from annotation, ignore from options, serializeNull from module.
         */
        @Test
        @DisplayName("F.13 Different properties from different sources")
        void differentPropertiesFromDifferentSources() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(featureConfig("Person", "firstName", Map.of("key", "first_name")))
                    .moduleProperties(Map.of("serializeNull", true))
                    .optionsProperties(featureConfig("Person", "firstName", Map.of("ignore", true)))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertEquals("first_name", config.getKey(), "from annotation");
            assertTrue(config.isSerializeNull(), "from module (global scope)");
            assertTrue(config.isIgnore(), "from options");
        }

        /**
         * Spec: Dynamic runtime overrides static model for Feature.
         */
        @Test
        @DisplayName("F.14 Dynamic (runtime) overrides static (model) for Feature ignore")
        void dynamicOverridesStatic() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(featureConfig("Person", "firstName", Map.of("ignore", true)))
                    .optionsProperties(featureConfig("Person", "firstName", Map.of("ignore", false)))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertFalse(config.isIgnore(),
                    "Spec: Runtime options override annotation for Feature ignore");
        }

        /**
         * Spec: Feature-level scope at higher source overrides feature-level scope at lower source.
         */
        @Test
        @DisplayName("F.15 Feature scope at options overrides feature scope at annotation")
        void featureScopeAtHigherSourceOverrides() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(featureConfig("Person", "firstName", Map.of("key", "annotation_name")))
                    .optionsProperties(featureConfig("Person", "firstName", Map.of("key", "options_name")))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertEquals("options_name", config.getKey());
        }

        /**
         * Spec: Class scope at higher source overrides feature scope at lower source.
         */
        @Test
        @DisplayName("F.16 Class scope at options overrides feature scope at annotation")
        void classScopeAtHigherSourceOverridesFeatureScopeAtLowerSource() {
            Map<String, Object> classProps = new HashMap<>();
            classProps.put("ignore", true);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(featureConfig("Person", "firstName", Map.of("ignore", false)))
                    .optionsProperties(Map.of("Person", classProps))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertTrue(config.isIgnore(),
                    "Spec: Class scope at options (higher) overrides feature scope at annotation (lower)");
        }
    }

    // ================================================================
    // Directional Visibility through Resolver
    // ================================================================

    @Nested
    @DisplayName("Directional Visibility through Resolver")
    class DirectionalVisibility {

        /**
         * Spec: All 5 directional visibility flags can be set through resolver.
         */
        @Test
        @DisplayName("F.17 All directional flags propagated through resolver")
        void allDirectionalFlags_propagated() {
            Map<String, Object> featureProps = new HashMap<>();
            featureProps.put("ignore", true);
            featureProps.put("ignoreRead", true);
            featureProps.put("ignoreWrite", true);
            featureProps.put("forceRead", true);
            featureProps.put("forceWrite", true);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(featureConfig("Person", "firstName", featureProps))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertTrue(config.isIgnore());
            assertTrue(config.isIgnoreRead());
            assertTrue(config.isIgnoreWrite());
            assertTrue(config.isForceRead());
            assertTrue(config.isForceWrite());
        }

        /**
         * Spec: ignore at global scope, forceWrite at feature scope.
         * Feature-level forceWrite wins → shouldSerialize=true.
         */
        @Test
        @DisplayName("F.18 Global ignore overridden by feature-level forceWrite")
        void globalIgnore_featureLevelForceWrite() {
            Map<String, Object> featureProps = new HashMap<>();
            featureProps.put("forceWrite", true);

            Map<String, Object> classProps = new HashMap<>();
            classProps.put("firstName", featureProps);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "ignore", true,
                            "Person", classProps
                    ))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertTrue(config.isIgnore(), "ignore from global scope");
            assertTrue(config.isForceWrite(), "forceWrite from feature scope");
            assertTrue(config.shouldSerialize(), "forceWrite overrides ignore");
        }

        /**
         * Spec: ignoreWrite at class scope applies to feature within that class.
         */
        @Test
        @DisplayName("F.19 Class-level ignoreWrite applies to feature")
        void classLevelIgnoreWrite_appliesToFeature() {
            Map<String, Object> classProps = new HashMap<>();
            classProps.put("ignoreWrite", true);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("Person", classProps))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertTrue(config.isIgnoreWrite());
            assertFalse(config.shouldSerialize());
            assertTrue(config.shouldDeserialize());
        }
    }

    // ================================================================
    // Feature-specific Properties through Resolver
    // ================================================================

    @Nested
    @DisplayName("Feature-specific Properties through Resolver")
    class FeatureSpecificProperties {

        /**
         * Spec: Serialize value flags propagated through resolver at global scope.
         */
        @Test
        @DisplayName("F.20 Serialize value flags propagated via global scope")
        void serializeFlags_globalScope() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "serializeNull", true,
                            "serializeEmpty", true,
                            "serializeDefault", true
                    ))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertTrue(config.isSerializeNull());
            assertTrue(config.isSerializeEmpty());
            assertTrue(config.isSerializeDefault());
        }

        /**
         * Spec: enumSerialization can be set through resolver.
         */
        @Test
        @DisplayName("F.21 enumSerialization propagated through resolver")
        void enumSerialization_propagated() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("enumSerialization", "NAME"))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertEquals(EnumSerializationStrategy.NAME, config.getEnumSerialization());
        }

        /**
         * Spec: valueWriterName/valueReaderName propagated through resolver at feature scope.
         */
        @Test
        @DisplayName("F.22 ValueWriter/ValueReader names propagated at feature scope")
        void valueWriterReaderNames_featureScope() {
            Map<String, Object> featureProps = new HashMap<>();
            featureProps.put("valueWriterName", "isoDateWriter");
            featureProps.put("valueReaderName", "isoDateReader");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(featureConfig("Person", "firstName", featureProps))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertEquals("isoDateWriter", config.getValueWriterName());
            assertEquals("isoDateReader", config.getValueReaderName());
        }

        /**
         * Spec: key customization propagated through resolver.
         */
        @Test
        @DisplayName("F.23 Custom key propagated through resolver")
        void customKey_propagated() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(featureConfig("Person", "firstName", Map.of("key", "given_name")))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertEquals("given_name", config.getKey());
        }
    }

    // ================================================================
    // Caching for Feature
    // ================================================================

    @Nested
    @DisplayName("Caching")
    class Caching {

        /**
         * Spec §8: Configuration is resolved once, cached.
         */
        @Test
        @DisplayName("F.24 Feature config is cached for same feature")
        void featureConfigIsCached() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            FeatureConfig first = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);
            FeatureConfig second = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertSame(first, second,
                    "Spec §8: Feature config should be resolved once and cached");
        }

        /**
         * Spec §8: Different features get different cache entries.
         */
        @Test
        @DisplayName("F.25 Different features get different cache entries")
        void differentFeatures_differentCacheEntries() {
            Map<String, Object> firstNameProps = new HashMap<>();
            firstNameProps.put("key", "given_name");

            Map<String, Object> lastNameProps = new HashMap<>();
            lastNameProps.put("key", "family_name");

            Map<String, Object> classProps = new HashMap<>();
            classProps.put("firstName", firstNameProps);
            classProps.put("lastName", lastNameProps);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("Person", classProps))
                    .build();

            FeatureConfig firstNameConfig = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);
            FeatureConfig lastNameConfig = resolver.resolveFeatureConfig(lastNameAttr, diagnostics);

            assertEquals("given_name", firstNameConfig.getKey());
            assertEquals("family_name", lastNameConfig.getKey());
        }

        /**
         * Spec §8: clearCaches allows re-resolution.
         */
        @Test
        @DisplayName("F.26 clearCaches allows re-resolution of Feature config")
        void clearCachesAllowsReResolution() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            FeatureConfig first = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);
            resolver.clearCaches();
            FeatureConfig second = resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertNotNull(second);
            assertEquals(first.isIgnore(), second.isIgnore());
        }
    }

    // ================================================================
    // Validation Integration for Feature
    // ================================================================

    @Nested
    @DisplayName("Validation Integration")
    class ValidationIntegration {

        /**
         * Spec: Contradictory flags produce WARNINGs during resolution.
         */
        @Test
        @DisplayName("F.27 Contradictory ignore + forceWrite produces WARNING during resolution")
        void contradictoryFlags_warningDuringResolution() {
            Map<String, Object> featureProps = new HashMap<>();
            featureProps.put("ignore", true);
            featureProps.put("forceWrite", true);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(featureConfig("Person", "firstName", featureProps))
                    .build();

            resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                    "Spec: ignore + forceWrite must produce WARNING during resolution");
        }

        /**
         * Spec: Valid config produces no diagnostics.
         */
        @Test
        @DisplayName("F.28 Valid config produces no diagnostics")
        void validConfig_noDiagnostics() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(featureConfig("Person", "firstName",
                            Map.of("key", "given_name", "serializeNull", true)))
                    .build();

            resolver.resolveFeatureConfig(firstNameAttr, diagnostics);

            assertFalse(diagnostics.hasErrors());
            assertFalse(diagnostics.hasWarnings());
        }
    }

    // ================================================================
    // Global Feature Config (no feature context)
    // ================================================================

    @Nested
    @DisplayName("Global Feature Config (no feature context)")
    class GlobalFeatureConfig {

        /**
         * Spec: resolveGlobalFeatureConfig returns global-only config.
         */
        @Test
        @DisplayName("F.29 Global feature config resolves global-only properties")
        void globalFeatureConfig_resolvesGlobalOnly() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "serializeNull", true,
                            "enumSerialization", "VALUE"
                    ))
                    .build();

            FeatureConfig config = resolver.resolveGlobalFeatureConfig(diagnostics);

            assertTrue(config.isSerializeNull());
            assertEquals(EnumSerializationStrategy.VALUE, config.getEnumSerialization());
        }

        /**
         * Spec: Global feature config does not include class-scoped properties.
         */
        @Test
        @DisplayName("F.30 Global feature config ignores class-scoped properties")
        void globalFeatureConfig_ignoresClassScope() {
            Map<String, Object> classProps = new HashMap<>();
            classProps.put("serializeNull", true);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("Person", classProps))
                    .build();

            FeatureConfig config = resolver.resolveGlobalFeatureConfig(diagnostics);

            assertFalse(config.isSerializeNull(),
                    "Global config should NOT include class-scoped properties");
        }
    }

    // ================================================================
    // Helper Methods
    // ================================================================

    /**
     * Creates a property map with feature-scoped configuration.
     * Structure: { "ClassName": { "featureName": { ...featureProps } } }
     */
    private Map<String, Object> featureConfig(String className, String featureName,
            Map<String, Object> featureProps) {
        Map<String, Object> classMap = new HashMap<>();
        classMap.put(featureName, new HashMap<>(featureProps));

        Map<String, Object> result = new HashMap<>();
        result.put(className, classMap);
        return result;
    }
}
