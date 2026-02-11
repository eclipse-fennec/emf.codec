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
import org.eclipse.fennec.codec.config.DiscriminatorConfig;
import org.eclipse.fennec.codec.config.DiscriminatorConfig.FallbackStrategy;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Spec-based resolver tests for Discriminator configuration.
 * <p>
 * Verifies that {@link ConfigurationResolver#resolveDiscriminatorConfig} applies the
 * two-dimensional resolution (Source Hierarchy × Scope Chain) correctly.
 * <p>
 * Spec references:
 * <ul>
 *   <li>{@code docs/codec-v2-spec/02-config-resolution.md} - Configuration Resolution</li>
 *   <li>{@code docs/codec-v2-spec/08-discriminator-mapping.md} - Discriminator Mapping (§3)</li>
 * </ul>
 */
@DisplayName("Discriminator Configuration Resolver Spec Tests (spec 08-discriminator-mapping.md §3)")
class DiscriminatorConfigResolverSpecTest {

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

    // ----------------------------------------------------------------
    // Source Hierarchy (Vertical) for Discriminator
    // ----------------------------------------------------------------

    /**
     * Spec §3: Default Discriminator Settings — all defaults from built-in level.
     */
    @Test
    @DisplayName("DM.1 Built-in defaults when no configuration provided")
    void builtInDefaults() {
        ConfigurationResolver resolver = ConfigurationResolver.defaults();

        DiscriminatorConfig config = resolver.resolveDiscriminatorConfig(personClass, diagnostics);

        assertNull(config.getTypeMapId());
        assertNull(config.getTypeDiscriminatorPath());
        assertNull(config.getTypeDiscriminator());
        assertTrue(config.getTypeMappings().isEmpty());
        assertTrue(config.getInlineMappings().isEmpty());
        assertEquals(FallbackStrategy.SKIP, config.getFallbackStrategy());
        assertNull(config.getFallbackEClass());
        assertFalse(config.hasTypeMappingRegistry());
        assertFalse(config.hasInlineMapping());
    }

    /**
     * Spec: Options (priority 1) overrides all lower sources for Discriminator config.
     */
    @Test
    @DisplayName("DM.2 Options overrides all lower sources")
    void optionsOverridesAllLowerSources() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .annotationProperties(Map.of("typeDiscriminatorPath", "annotation.path"))
                .moduleProperties(Map.of("typeDiscriminatorPath", "module.path"))
                .factoryProperties(Map.of("typeDiscriminatorPath", "factory.path"))
                .resourceProperties(Map.of("typeDiscriminatorPath", "resource.path"))
                .optionsProperties(Map.of("typeDiscriminatorPath", "options.path"))
                .build();

        DiscriminatorConfig config = resolver.resolveDiscriminatorConfig(personClass, diagnostics);

        assertEquals("options.path", config.getTypeDiscriminatorPath(),
                "Spec: Load/Save Options has highest priority for Discriminator config");
    }

    /**
     * Spec: Resource (priority 2) overrides lower sources.
     */
    @Test
    @DisplayName("DM.3 Resource overrides factory, module, annotation")
    void resourceOverridesLowerLevels() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .annotationProperties(Map.of("typeDiscriminatorPath", "annotation.path"))
                .moduleProperties(Map.of("typeDiscriminatorPath", "module.path"))
                .factoryProperties(Map.of("typeDiscriminatorPath", "factory.path"))
                .resourceProperties(Map.of("typeDiscriminatorPath", "resource.path"))
                .build();

        DiscriminatorConfig config = resolver.resolveDiscriminatorConfig(personClass, diagnostics);

        assertEquals("resource.path", config.getTypeDiscriminatorPath());
    }

    /**
     * Spec: Factory (priority 3) overrides module, annotation.
     */
    @Test
    @DisplayName("DM.4 Factory overrides module, annotation")
    void factoryOverridesLowerLevels() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .annotationProperties(Map.of("typeDiscriminatorPath", "annotation.path"))
                .moduleProperties(Map.of("typeDiscriminatorPath", "module.path"))
                .factoryProperties(Map.of("typeDiscriminatorPath", "factory.path"))
                .build();

        DiscriminatorConfig config = resolver.resolveDiscriminatorConfig(personClass, diagnostics);

        assertEquals("factory.path", config.getTypeDiscriminatorPath());
    }

    /**
     * Spec: Module (priority 4) overrides annotation.
     */
    @Test
    @DisplayName("DM.5 Module overrides annotation")
    void moduleOverridesAnnotation() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .annotationProperties(Map.of("typeDiscriminatorPath", "annotation.path"))
                .moduleProperties(Map.of("typeDiscriminatorPath", "module.path"))
                .build();

        DiscriminatorConfig config = resolver.resolveDiscriminatorConfig(personClass, diagnostics);

        assertEquals("module.path", config.getTypeDiscriminatorPath());
    }

    /**
     * Spec: Annotation (priority 5) overrides built-in default.
     */
    @Test
    @DisplayName("DM.6 Annotation overrides built-in default")
    void annotationOverridesDefault() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .annotationProperties(Map.of("typeDiscriminatorPath", "annotation.path"))
                .build();

        DiscriminatorConfig config = resolver.resolveDiscriminatorConfig(personClass, diagnostics);

        assertEquals("annotation.path", config.getTypeDiscriminatorPath());
    }

    // ----------------------------------------------------------------
    // Scope Chain (Horizontal) for Discriminator
    // ----------------------------------------------------------------

    /**
     * Spec: Class scope overrides global scope for Discriminator.
     */
    @Test
    @DisplayName("DM.7 Class scope overrides global scope")
    void classScopeOverridesGlobalScope() {
        Map<String, Object> classConfig = new HashMap<>();
        classConfig.put("fallbackStrategy", "ERROR");

        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(Map.of(
                        "fallbackStrategy", "SKIP",
                        "Person", classConfig
                ))
                .build();

        DiscriminatorConfig config = resolver.resolveDiscriminatorConfig(personClass, diagnostics);

        assertEquals(FallbackStrategy.ERROR, config.getFallbackStrategy(),
                "Spec: Class scope overrides global scope for Discriminator config");
    }

    /**
     * Spec: Different EClasses resolve different Discriminator configs.
     */
    @Test
    @DisplayName("DM.8 Different EClasses get different Discriminator configs")
    void differentEClassesDifferentConfigs() {
        Map<String, Object> personConfig = new HashMap<>();
        personConfig.put("typeMapId", "person-registry");
        personConfig.put("typeDiscriminatorPath", "info.personType");

        Map<String, Object> addressConfig = new HashMap<>();
        addressConfig.put("typeMapId", "address-registry");
        addressConfig.put("typeDiscriminatorPath", "info.addressType");

        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(Map.of(
                        "Person", personConfig,
                        "Address", addressConfig
                ))
                .build();

        DiscriminatorConfig personDiscConfig = resolver.resolveDiscriminatorConfig(personClass, diagnostics);
        DiscriminatorConfig addressDiscConfig = resolver.resolveDiscriminatorConfig(addressClass, diagnostics);

        assertEquals("person-registry", personDiscConfig.getTypeMapId());
        assertEquals("info.personType", personDiscConfig.getTypeDiscriminatorPath());
        assertEquals("address-registry", addressDiscConfig.getTypeMapId());
        assertEquals("info.addressType", addressDiscConfig.getTypeDiscriminatorPath());
    }

    // ----------------------------------------------------------------
    // Combined Resolution for Discriminator
    // ----------------------------------------------------------------

    /**
     * Spec: Different Discriminator properties can come from different levels.
     */
    @Test
    @DisplayName("DM.9 Different properties from different sources")
    void differentPropertiesFromDifferentSources() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .annotationProperties(Map.of("typeMapId", "my-registry"))
                .moduleProperties(Map.of("typeDiscriminatorPath", "info.type"))
                .optionsProperties(Map.of("fallbackStrategy", "ERROR"))
                .build();

        DiscriminatorConfig config = resolver.resolveDiscriminatorConfig(personClass, diagnostics);

        assertEquals("my-registry", config.getTypeMapId(), "from annotation");
        assertEquals("info.type", config.getTypeDiscriminatorPath(), "from module");
        assertEquals(FallbackStrategy.ERROR, config.getFallbackStrategy(), "from options");
    }

    /**
     * Spec: Dynamic runtime overrides static model for Discriminator.
     */
    @Test
    @DisplayName("DM.10 Dynamic (runtime) overrides static (model) for fallbackStrategy")
    void dynamicOverridesStatic() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .annotationProperties(Map.of("fallbackStrategy", "SKIP"))
                .optionsProperties(Map.of("fallbackStrategy", "ERROR"))
                .build();

        DiscriminatorConfig config = resolver.resolveDiscriminatorConfig(personClass, diagnostics);

        assertEquals(FallbackStrategy.ERROR, config.getFallbackStrategy(),
                "Spec: Runtime options override annotation for fallbackStrategy");
    }

    // ----------------------------------------------------------------
    // Caching for Discriminator
    // ----------------------------------------------------------------

    /**
     * Spec §8: Configuration is resolved once, cached.
     */
    @Test
    @DisplayName("DM.11 Discriminator config is cached for same EClass")
    void discriminatorConfigIsCached() {
        ConfigurationResolver resolver = ConfigurationResolver.defaults();

        DiscriminatorConfig first = resolver.resolveDiscriminatorConfig(personClass, diagnostics);
        DiscriminatorConfig second = resolver.resolveDiscriminatorConfig(personClass, diagnostics);

        assertSame(first, second,
                "Spec §8: Discriminator config should be resolved once and cached");
    }

    /**
     * Spec §8: clearCaches allows re-resolution.
     */
    @Test
    @DisplayName("DM.12 clearCaches allows re-resolution of Discriminator config")
    void clearCachesAllowsReResolution() {
        ConfigurationResolver resolver = ConfigurationResolver.defaults();

        DiscriminatorConfig first = resolver.resolveDiscriminatorConfig(personClass, diagnostics);
        resolver.clearCaches();
        DiscriminatorConfig second = resolver.resolveDiscriminatorConfig(personClass, diagnostics);

        assertNotNull(second);
        assertEquals(first.getFallbackStrategy(), second.getFallbackStrategy());
    }

    // ----------------------------------------------------------------
    // Validation Integration for Discriminator
    // ----------------------------------------------------------------

    /**
     * Spec: typeDiscriminator without typeMapId produces ERROR during resolution.
     */
    @Test
    @DisplayName("DM.13 Validation called: typeDiscriminator without typeMapId triggers ERROR")
    void validationCalled_typeDiscriminatorWithoutMapId() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(Map.of(
                        "typeDiscriminator", "my-discriminator"
                        // typeMapId intentionally not set
                ))
                .build();

        resolver.resolveDiscriminatorConfig(personClass, diagnostics);

        assertTrue(diagnostics.hasErrors(),
                "Spec: typeDiscriminator without typeMapId must produce ERROR during resolution");
    }

    /**
     * Spec: fallbackEClass with non-FALLBACK strategy produces WARNING during resolution.
     */
    @Test
    @DisplayName("DM.14 Validation called: fallbackEClass with SKIP strategy triggers WARNING")
    void validationCalled_fallbackEClassWithSkip() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(Map.of(
                        "fallbackStrategy", "SKIP",
                        "fallbackEClass", "http://example.org#//Default"
                ))
                .build();

        resolver.resolveDiscriminatorConfig(personClass, diagnostics);

        assertTrue(diagnostics.hasWarnings(),
                "Spec: fallbackEClass with non-FALLBACK strategy should produce WARNING during resolution");
    }

    // ----------------------------------------------------------------
    // Discriminator-specific properties through resolver
    // ----------------------------------------------------------------

    /**
     * Spec §3: typeMapId propagated through resolver.
     */
    @Test
    @DisplayName("DM.15 typeMapId propagated through resolver")
    void typeMapId_propagated() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(Map.of("typeMapId", "lorawan-devices"))
                .build();

        DiscriminatorConfig config = resolver.resolveDiscriminatorConfig(personClass, diagnostics);

        assertEquals("lorawan-devices", config.getTypeMapId());
    }

    /**
     * Spec §3: typeDiscriminator propagated through resolver.
     */
    @Test
    @DisplayName("DM.16 typeDiscriminator propagated through resolver")
    void typeDiscriminator_propagated() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(Map.of(
                        "typeMapId", "my-registry",
                        "typeDiscriminator", "sensor-type"
                ))
                .build();

        DiscriminatorConfig config = resolver.resolveDiscriminatorConfig(personClass, diagnostics);

        assertEquals("sensor-type", config.getTypeDiscriminator());
        assertTrue(config.isRegisteredWithRegistry());
    }

    /**
     * Spec §3: fallbackEClass with FALLBACK strategy propagated through resolver.
     */
    @Test
    @DisplayName("DM.17 fallbackEClass with FALLBACK strategy propagated through resolver")
    void fallbackEClass_propagated() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(Map.of(
                        "fallbackStrategy", "FALLBACK",
                        "fallbackEClass", "http://example.org#//DefaultEntity"
                ))
                .build();

        DiscriminatorConfig config = resolver.resolveDiscriminatorConfig(personClass, diagnostics);

        assertEquals(FallbackStrategy.FALLBACK, config.getFallbackStrategy());
        assertEquals("http://example.org#//DefaultEntity", config.getFallbackEClass());
    }
}
