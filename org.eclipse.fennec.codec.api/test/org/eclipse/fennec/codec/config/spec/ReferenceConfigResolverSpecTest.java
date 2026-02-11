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
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.ReferenceConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.model.metadata.SerializationFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Spec-based resolver tests for Reference configuration.
 * <p>
 * Verifies that {@link ConfigurationResolver#resolveReferenceConfig} applies the
 * two-dimensional resolution (Source Hierarchy × Scope Chain) correctly for
 * reference properties. Reference config has a 3-level scope chain:
 * Global → EClass → Feature (EReference).
 * <p>
 * Spec references:
 * <ul>
 *   <li>{@code docs/codec-v2-spec/02-config-resolution.md} - Configuration Resolution</li>
 *   <li>{@code docs/codec-v2-spec/10-reference.md} - Reference Serialization (§3, §8, §10)</li>
 * </ul>
 */
@DisplayName("ReferenceConfig Resolver Spec Tests (spec 10-reference.md)")
class ReferenceConfigResolverSpecTest {

    private DiagnosticCollector diagnostics;
    private EClass personClass;
    private EClass companyClass;
    private EReference employerRef;
    private EReference addressRef;

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

        companyClass = EcoreFactory.eINSTANCE.createEClass();
        companyClass.setName("Company");
        testPackage.getEClassifiers().add(companyClass);

        // Person.employer -> Company (non-containment)
        employerRef = EcoreFactory.eINSTANCE.createEReference();
        employerRef.setName("employer");
        employerRef.setEType(companyClass);
        employerRef.setContainment(false);
        personClass.getEStructuralFeatures().add(employerRef);

        // Person.address -> Address placeholder (non-containment)
        EClass addressClass = EcoreFactory.eINSTANCE.createEClass();
        addressClass.setName("Address");
        testPackage.getEClassifiers().add(addressClass);

        addressRef = EcoreFactory.eINSTANCE.createEReference();
        addressRef.setName("address");
        addressRef.setEType(addressClass);
        addressRef.setContainment(false);
        personClass.getEStructuralFeatures().add(addressRef);
    }

    // ================================================================
    // Source Hierarchy (Vertical)
    // Spec: Options > Resource > Factory > Module > Annotation > Built-in
    // ================================================================

    @Nested
    @DisplayName("Source Hierarchy")
    class SourceHierarchy {

        /**
         * Spec §8: Built-in defaults when no configuration provided.
         */
        @Test
        @DisplayName("R.1 Built-in defaults when no configuration provided")
        void builtInDefaults() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            ReferenceConfig config = resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
            assertEquals("$ref", config.getRefKey());
            assertEquals("_type", config.getRefTypeKey());
            assertEquals("$proxy", config.getProxyKey());
            assertFalse(config.isExpand());
            assertFalse(config.isExpandGlobal());
            assertEquals(1, config.getExpandDepth());
            assertTrue(config.isExpandIgnoreBidirectional());
            assertTrue(config.isSerializeInstanceType());
            assertNull(config.getValueReaderName());
            assertNull(config.getValueWriterName());
        }

        /**
         * Spec: Options (priority 1) overrides all lower sources.
         */
        @Test
        @DisplayName("R.2 Options overrides all lower sources")
        void optionsOverridesAllLowerSources() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("refFormat", "PLAIN"))
                    .moduleProperties(Map.of("refFormat", "STRUCTURED"))
                    .factoryProperties(Map.of("refFormat", "PLAIN"))
                    .resourceProperties(Map.of("refFormat", "STRUCTURED"))
                    .optionsProperties(Map.of("refFormat", "PLAIN"))
                    .build();

            ReferenceConfig config = resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertEquals(SerializationFormat.PLAIN, config.getFormat(),
                    "Spec: Load/Save Options has highest priority");
        }

        /**
         * Spec: Resource (priority 2) overrides lower sources.
         */
        @Test
        @DisplayName("R.3 Resource overrides factory, module, annotation")
        void resourceOverridesLowerLevels() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("refKey", "ann"))
                    .moduleProperties(Map.of("refKey", "mod"))
                    .factoryProperties(Map.of("refKey", "fac"))
                    .resourceProperties(Map.of("refKey", "res"))
                    .build();

            ReferenceConfig config = resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertEquals("res", config.getRefKey());
        }

        /**
         * Spec: Factory (priority 3) overrides module, annotation.
         */
        @Test
        @DisplayName("R.4 Factory overrides module, annotation")
        void factoryOverridesLowerLevels() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("refKey", "ann"))
                    .moduleProperties(Map.of("refKey", "mod"))
                    .factoryProperties(Map.of("refKey", "fac"))
                    .build();

            ReferenceConfig config = resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertEquals("fac", config.getRefKey());
        }

        /**
         * Spec: Module (priority 4) overrides annotation.
         */
        @Test
        @DisplayName("R.5 Module overrides annotation")
        void moduleOverridesAnnotation() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("refKey", "ann"))
                    .moduleProperties(Map.of("refKey", "mod"))
                    .build();

            ReferenceConfig config = resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertEquals("mod", config.getRefKey());
        }

        /**
         * Spec: Annotation (priority 5) overrides built-in default.
         */
        @Test
        @DisplayName("R.6 Annotation overrides built-in default")
        void annotationOverridesDefault() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("refKey", "ann"))
                    .build();

            ReferenceConfig config = resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertEquals("ann", config.getRefKey());
        }
    }

    // ================================================================
    // Scope Chain (Horizontal)
    // Spec: Global → EClass → Feature (EReference)
    // Note: EClass scope is valid for reference config per spec §3.1
    // ================================================================

    @Nested
    @DisplayName("Scope Chain")
    class ScopeChain {

        /**
         * Spec §3.1: Global scope applies to all references.
         */
        @Test
        @DisplayName("R.7 Global scope applies to all references")
        void globalScope_appliesToAllReferences() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("refFormat", "PLAIN"))
                    .build();

            ReferenceConfig employerConfig = resolver.resolveReferenceConfig(employerRef, diagnostics);
            ReferenceConfig addressConfig = resolver.resolveReferenceConfig(addressRef, diagnostics);

            assertEquals(SerializationFormat.PLAIN, employerConfig.getFormat());
            assertEquals(SerializationFormat.PLAIN, addressConfig.getFormat());
        }

        /**
         * Spec §3.1: Feature scope overrides class scope overrides global scope.
         */
        @Test
        @DisplayName("R.8 Feature scope overrides class and global scope")
        void featureScope_overridesClassAndGlobal() {
            Map<String, Object> personConfig = new HashMap<>();
            personConfig.put("refKey", "classRef");

            Map<String, Object> employerConfig = new HashMap<>();
            employerConfig.put("refKey", "featureRef");
            personConfig.put("employer", employerConfig);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "refKey", "globalRef",
                            "Person", personConfig
                    ))
                    .build();

            ReferenceConfig config = resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertEquals("featureRef", config.getRefKey(),
                    "Feature scope should override class and global scope");
        }

        /**
         * Spec: Different references on the same class resolve independently.
         */
        @Test
        @DisplayName("R.9 Different references on same class resolve independently")
        void differentReferences_resolveIndependently() {
            Map<String, Object> personConfig = new HashMap<>();

            Map<String, Object> employerFeatureConfig = new HashMap<>();
            employerFeatureConfig.put("refFormat", "PLAIN");
            personConfig.put("employer", employerFeatureConfig);

            Map<String, Object> addressFeatureConfig = new HashMap<>();
            addressFeatureConfig.put("refFormat", "STRUCTURED");
            personConfig.put("address", addressFeatureConfig);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("Person", personConfig))
                    .build();

            ReferenceConfig employerCfg = resolver.resolveReferenceConfig(employerRef, diagnostics);
            ReferenceConfig addressCfg = resolver.resolveReferenceConfig(addressRef, diagnostics);

            assertEquals(SerializationFormat.PLAIN, employerCfg.getFormat());
            assertEquals(SerializationFormat.STRUCTURED, addressCfg.getFormat());
        }

        /**
         * Spec: Class scope overrides global scope.
         */
        @Test
        @DisplayName("R.10 Class scope overrides global scope")
        void classScope_overridesGlobalScope() {
            Map<String, Object> personConfig = new HashMap<>();
            personConfig.put("refKey", "classRef");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "refKey", "globalRef",
                            "Person", personConfig
                    ))
                    .build();

            ReferenceConfig config = resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertEquals("classRef", config.getRefKey(),
                    "Class scope should override global scope");
        }
    }

    // ================================================================
    // Combined Resolution
    // ================================================================

    @Nested
    @DisplayName("Combined Resolution")
    class CombinedResolution {

        /**
         * Spec: Different properties from different sources.
         */
        @Test
        @DisplayName("R.11 Different properties from different sources")
        void differentPropertiesFromDifferentSources() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("refFormat", "PLAIN"))
                    .moduleProperties(Map.of("refKey", "moduleRef"))
                    .optionsProperties(Map.of("expand", true))
                    .build();

            ReferenceConfig config = resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertEquals(SerializationFormat.PLAIN, config.getFormat(), "from annotation");
            assertEquals("moduleRef", config.getRefKey(), "from module");
            assertTrue(config.isExpand(), "from options");
        }

        /**
         * Spec: Dynamic runtime overrides static model.
         */
        @Test
        @DisplayName("R.12 Dynamic (runtime) overrides static (model)")
        void dynamicOverridesStatic() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("refFormat", "PLAIN"))
                    .optionsProperties(Map.of("refFormat", "STRUCTURED"))
                    .build();

            ReferenceConfig config = resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertEquals(SerializationFormat.STRUCTURED, config.getFormat(),
                    "Spec: Runtime options override annotation");
        }
    }

    // ================================================================
    // Caching
    // ================================================================

    @Nested
    @DisplayName("Caching")
    class Caching {

        /**
         * Spec §8: Configuration is resolved once, cached.
         */
        @Test
        @DisplayName("R.13 Reference config is cached for same feature")
        void referenceConfigIsCached() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            ReferenceConfig first = resolver.resolveReferenceConfig(employerRef, diagnostics);
            ReferenceConfig second = resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertSame(first, second,
                    "Spec: Reference config should be resolved once and cached");
        }

        /**
         * Spec §8: clearCaches allows re-resolution.
         */
        @Test
        @DisplayName("R.14 clearCaches allows re-resolution")
        void clearCachesAllowsReResolution() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            ReferenceConfig first = resolver.resolveReferenceConfig(employerRef, diagnostics);
            resolver.clearCaches();
            ReferenceConfig second = resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertNotNull(second);
            assertEquals(first.getFormat(), second.getFormat());
        }

        /**
         * Spec: Different features get different cache entries.
         */
        @Test
        @DisplayName("R.15 Different features have separate cache entries")
        void differentFeatures_separateCacheEntries() {
            Map<String, Object> personConfig = new HashMap<>();

            Map<String, Object> employerFeatureConfig = new HashMap<>();
            employerFeatureConfig.put("refFormat", "PLAIN");
            personConfig.put("employer", employerFeatureConfig);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("Person", personConfig))
                    .build();

            ReferenceConfig employerCfg = resolver.resolveReferenceConfig(employerRef, diagnostics);
            ReferenceConfig addressCfg = resolver.resolveReferenceConfig(addressRef, diagnostics);

            // Different features, different configs
            assertEquals(SerializationFormat.PLAIN, employerCfg.getFormat());
            assertEquals(SerializationFormat.STRUCTURED, addressCfg.getFormat());
        }
    }

    // ================================================================
    // Validation Integration
    // ================================================================

    @Nested
    @DisplayName("Validation Integration")
    class ValidationIntegration {

        /**
         * Spec R-V5: Validation called during resolution.
         */
        @Test
        @DisplayName("R.16 R-V5 validation: refTypeKey + PLAIN triggers WARNING during resolution")
        void rv5_validationDuringResolution() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "refFormat", "PLAIN",
                            "refTypeKey", "customType"
                    ))
                    .build();

            resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                    "Spec R-V5: Custom refTypeKey with PLAIN should produce WARNING during resolution");
        }

        /**
         * Spec: Valid config produces no diagnostics.
         */
        @Test
        @DisplayName("R.17 Valid config produces no diagnostics")
        void validConfig_noDiagnostics() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "refFormat", "STRUCTURED",
                            "refKey", "customRef",
                            "expand", true
                    ))
                    .build();

            resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertFalse(diagnostics.hasErrors());
            assertFalse(diagnostics.hasWarnings());
        }
    }

    // ================================================================
    // Reference-Specific Properties Through Resolver
    // ================================================================

    @Nested
    @DisplayName("Reference-Specific Properties")
    class ReferenceSpecificProperties {

        /**
         * Spec: expand can be set through resolver.
         */
        @Test
        @DisplayName("R.18 expand propagated through resolver")
        void expand_propagated() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("expand", true))
                    .build();

            ReferenceConfig config = resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertTrue(config.isExpand());
            assertTrue(config.shouldExpand());
        }

        /**
         * Spec: expandGlobal can be set through resolver.
         */
        @Test
        @DisplayName("R.19 expandGlobal propagated through resolver")
        void expandGlobal_propagated() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("expandGlobal", true))
                    .build();

            ReferenceConfig config = resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertTrue(config.isExpandGlobal());
            assertTrue(config.shouldExpand());
        }

        /**
         * Spec: ValueWriter/ValueReader names propagated through resolver.
         */
        @Test
        @DisplayName("R.20 ValueWriter/ValueReader names propagated through resolver")
        void valueWriterReaderNames_propagated() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "valueWriterName", "myRefWriter",
                            "valueReaderName", "myRefReader"
                    ))
                    .build();

            ReferenceConfig config = resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertEquals("myRefWriter", config.getValueWriterName());
            assertEquals("myRefReader", config.getValueReaderName());
        }

        /**
         * Spec: serializeInstanceType propagated through resolver.
         */
        @Test
        @DisplayName("R.21 serializeInstanceType propagated through resolver")
        void serializeInstanceType_propagated() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("serializeInstanceType", false))
                    .build();

            ReferenceConfig config = resolver.resolveReferenceConfig(employerRef, diagnostics);

            assertFalse(config.isSerializeInstanceType());
        }
    }

    // ================================================================
    // Global Reference Config
    // ================================================================

    @Nested
    @DisplayName("Global Reference Config")
    class GlobalReferenceConfig {

        /**
         * Spec: Global reference config applies when no feature context.
         */
        @Test
        @DisplayName("R.22 Global reference config resolved without feature context")
        void globalReferenceConfig_resolved() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("refFormat", "PLAIN"))
                    .build();

            ReferenceConfig config = resolver.resolveGlobalReferenceConfig(diagnostics);

            assertEquals(SerializationFormat.PLAIN, config.getFormat());
        }

        /**
         * Spec: Global reference config is cached.
         */
        @Test
        @DisplayName("R.23 Global reference config is cached")
        void globalReferenceConfig_cached() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            ReferenceConfig first = resolver.resolveGlobalReferenceConfig(diagnostics);
            ReferenceConfig second = resolver.resolveGlobalReferenceConfig(diagnostics);

            assertSame(first, second);
        }
    }
}
