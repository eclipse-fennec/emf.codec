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
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.IdConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.metadata.model.codec.IdKeyMode;
import org.eclipse.fennec.codec.metadata.model.codec.IdStrategy;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Spec-based resolver tests for ID configuration.
 * <p>
 * Verifies that {@link ConfigurationResolver#resolveIdConfig} applies the
 * two-dimensional resolution (Source Hierarchy × Scope Chain) correctly.
 * <p>
 * Spec references:
 * <ul>
 *   <li>{@code docs/codec-v2-spec/02-config-resolution.md} - Configuration Resolution</li>
 *   <li>{@code docs/codec-v2-spec/09-id.md} - ID Serialization (§5.0, §12, §13.1)</li>
 * </ul>
 */
@DisplayName("ID Configuration Resolver Spec Tests (spec 09-id.md §5.0, §12)")
class IdConfigResolverSpecTest {

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
    // Source Hierarchy (Vertical) for ID
    // ----------------------------------------------------------------

    /**
     * Spec §12: Default ID Settings — all defaults from built-in level.
     */
    @Test
    @DisplayName("ID.1 Built-in defaults when no configuration provided")
    void builtInDefaults() {
        ConfigurationResolver resolver = ConfigurationResolver.defaults();

        IdConfig config = resolver.resolveIdConfig(personClass, diagnostics);

        assertEquals("_id", config.getKey());
        assertEquals(IdStrategy.ID_FIELD, config.getStrategy());
        assertEquals(IdKeyMode.ID_ONLY, config.getKeyMode());
        assertEquals(SerializationFormat.PLAIN, config.getFormat());
        assertEquals("id", config.getValueKey());
        assertEquals("-", config.getSeparator());
        assertEquals("separator", config.getSeparatorKey());
        assertTrue(config.isSerializeSeparator());
        assertTrue(config.isOnTop());
    }

    /**
     * Spec: Options (priority 1) overrides all lower sources for ID config.
     */
    @Test
    @DisplayName("ID.2 Options overrides all lower sources")
    void optionsOverridesAllLowerSources() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .annotationProperties(Map.of("idKey", "annotation"))
                .moduleProperties(Map.of("idKey", "module"))
                .factoryProperties(Map.of("idKey", "factory"))
                .resourceProperties(Map.of("idKey", "resource"))
                .optionsProperties(Map.of("idKey", "options"))
                .build();

        IdConfig config = resolver.resolveIdConfig(personClass, diagnostics);

        assertEquals("options", config.getKey(),
                "Spec: Load/Save Options has highest priority for ID config");
    }

    /**
     * Spec: Resource (priority 2) overrides lower sources.
     */
    @Test
    @DisplayName("ID.3 Resource overrides factory, module, annotation")
    void resourceOverridesLowerLevels() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .annotationProperties(Map.of("idKey", "annotation"))
                .moduleProperties(Map.of("idKey", "module"))
                .factoryProperties(Map.of("idKey", "factory"))
                .resourceProperties(Map.of("idKey", "resource"))
                .build();

        IdConfig config = resolver.resolveIdConfig(personClass, diagnostics);

        assertEquals("resource", config.getKey());
    }

    /**
     * Spec: Factory (priority 3) overrides module, annotation.
     */
    @Test
    @DisplayName("ID.4 Factory overrides module, annotation")
    void factoryOverridesLowerLevels() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .annotationProperties(Map.of("idKey", "annotation"))
                .moduleProperties(Map.of("idKey", "module"))
                .factoryProperties(Map.of("idKey", "factory"))
                .build();

        IdConfig config = resolver.resolveIdConfig(personClass, diagnostics);

        assertEquals("factory", config.getKey());
    }

    /**
     * Spec: Module (priority 4) overrides annotation.
     */
    @Test
    @DisplayName("ID.5 Module overrides annotation")
    void moduleOverridesAnnotation() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .annotationProperties(Map.of("idKey", "annotation"))
                .moduleProperties(Map.of("idKey", "module"))
                .build();

        IdConfig config = resolver.resolveIdConfig(personClass, diagnostics);

        assertEquals("module", config.getKey());
    }

    /**
     * Spec: Annotation (priority 5) overrides built-in default.
     */
    @Test
    @DisplayName("ID.6 Annotation overrides built-in default")
    void annotationOverridesDefault() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .annotationProperties(Map.of("idKey", "annotation"))
                .build();

        IdConfig config = resolver.resolveIdConfig(personClass, diagnostics);

        assertEquals("annotation", config.getKey());
    }

    // ----------------------------------------------------------------
    // Scope Chain (Horizontal) for ID
    // ----------------------------------------------------------------

    /**
     * Spec: Class scope overrides global scope for ID.
     */
    @Test
    @DisplayName("ID.7 Class scope overrides global scope")
    void classScopeOverridesGlobalScope() {
        Map<String, Object> classConfig = new HashMap<>();
        classConfig.put("idKey", "personId");

        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(Map.of(
                        "idKey", "globalId",
                        "Person", classConfig
                ))
                .build();

        IdConfig config = resolver.resolveIdConfig(personClass, diagnostics);

        assertEquals("personId", config.getKey(),
                "Spec: Class scope overrides global scope for ID config");
    }

    /**
     * Spec: Different EClasses resolve different ID configs.
     */
    @Test
    @DisplayName("ID.8 Different EClasses get different ID configs")
    void differentEClassesDifferentConfigs() {
        Map<String, Object> personConfig = new HashMap<>();
        personConfig.put("idKey", "personId");
        personConfig.put("idStrategy", "COMBINED");
        personConfig.put("idFeatures", List.of("firstName", "lastName"));

        Map<String, Object> addressConfig = new HashMap<>();
        addressConfig.put("idKey", "addressId");
        addressConfig.put("idStrategy", "ID_FIELD");

        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(Map.of(
                        "Person", personConfig,
                        "Address", addressConfig
                ))
                .build();

        IdConfig personIdConfig = resolver.resolveIdConfig(personClass, diagnostics);
        IdConfig addressIdConfig = resolver.resolveIdConfig(addressClass, diagnostics);

        assertEquals("personId", personIdConfig.getKey());
        assertEquals(IdStrategy.COMBINED, personIdConfig.getStrategy());
        assertEquals("addressId", addressIdConfig.getKey());
        assertEquals(IdStrategy.ID_FIELD, addressIdConfig.getStrategy());
    }

    // ----------------------------------------------------------------
    // Combined Resolution for ID
    // ----------------------------------------------------------------

    /**
     * Spec: Different ID properties can come from different levels.
     * idKey from options, idStrategy from annotation, idFormat from module.
     */
    @Test
    @DisplayName("ID.9 Different properties from different sources")
    void differentPropertiesFromDifferentSources() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .annotationProperties(Map.of("idStrategy", "COMBINED",
                        "idFeatures", List.of("f1", "f2")))
                .moduleProperties(Map.of("idFormat", "STRUCTURED"))
                .optionsProperties(Map.of("idKey", "@id"))
                .build();

        IdConfig config = resolver.resolveIdConfig(personClass, diagnostics);

        assertEquals("@id", config.getKey(), "from options");
        assertEquals(IdStrategy.COMBINED, config.getStrategy(), "from annotation");
        assertEquals(SerializationFormat.STRUCTURED, config.getFormat(), "from module");
    }

    /**
     * Spec: Dynamic runtime overrides static model for ID.
     */
    @Test
    @DisplayName("ID.10 Dynamic (runtime) overrides static (model) for ID strategy")
    void dynamicOverridesStatic() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .annotationProperties(Map.of("idStrategy", "ID_FIELD"))
                .optionsProperties(Map.of("idStrategy", "COMBINED",
                        "idFeatures", List.of("f1", "f2")))
                .build();

        IdConfig config = resolver.resolveIdConfig(personClass, diagnostics);

        assertEquals(IdStrategy.COMBINED, config.getStrategy(),
                "Spec: Runtime options override annotation for ID strategy");
    }

    // ----------------------------------------------------------------
    // Caching for ID
    // ----------------------------------------------------------------

    /**
     * Spec §8: Configuration is resolved once, cached.
     */
    @Test
    @DisplayName("ID.11 ID config is cached for same EClass")
    void idConfigIsCached() {
        ConfigurationResolver resolver = ConfigurationResolver.defaults();

        IdConfig first = resolver.resolveIdConfig(personClass, diagnostics);
        IdConfig second = resolver.resolveIdConfig(personClass, diagnostics);

        assertSame(first, second,
                "Spec §8: ID config should be resolved once and cached");
    }

    /**
     * Spec §8: clearCaches allows re-resolution.
     */
    @Test
    @DisplayName("ID.12 clearCaches allows re-resolution of ID config")
    void clearCachesAllowsReResolution() {
        ConfigurationResolver resolver = ConfigurationResolver.defaults();

        IdConfig first = resolver.resolveIdConfig(personClass, diagnostics);
        resolver.clearCaches();
        IdConfig second = resolver.resolveIdConfig(personClass, diagnostics);

        assertNotNull(second);
        assertEquals(first.getKey(), second.getKey());
    }

    // ----------------------------------------------------------------
    // Validation Integration for ID
    // ----------------------------------------------------------------

    /**
     * Spec §13.1: COMBINED with empty idFeatures triggers ERROR during resolution.
     */
    @Test
    @DisplayName("ID.13 Validation called: COMBINED with empty idFeatures triggers ERROR")
    void validationCalled_combinedEmptyFeatures() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(Map.of(
                        "idStrategy", "COMBINED"
                        // idFeatures intentionally not set (empty)
                ))
                .build();

        resolver.resolveIdConfig(personClass, diagnostics);

        assertTrue(diagnostics.hasErrors(),
                "Spec §13.1: COMBINED with empty idFeatures must produce ERROR during resolution");
    }

    /**
     * Spec: idValueKey non-default with PLAIN format triggers WARNING during resolution.
     */
    @Test
    @DisplayName("ID.14 Validation called: non-default idValueKey with PLAIN triggers WARNING")
    void validationCalled_nonDefaultValueKeyPlain() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(Map.of(
                        "idFormat", "PLAIN",
                        "idValueKey", "customId"
                ))
                .build();

        resolver.resolveIdConfig(personClass, diagnostics);

        assertTrue(diagnostics.hasWarnings(),
                "Spec: idValueKey non-default with PLAIN should produce WARNING during resolution");
    }

    // ----------------------------------------------------------------
    // ID-specific properties through resolver
    // ----------------------------------------------------------------

    /**
     * Spec §2: IdKeyMode.NONE can be set through resolver.
     */
    @Test
    @DisplayName("ID.15 IdKeyMode.NONE propagated through resolver")
    void idKeyModeNone_propagated() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(Map.of("idKeyMode", "NONE"))
                .build();

        IdConfig config = resolver.resolveIdConfig(personClass, diagnostics);

        assertEquals(IdKeyMode.NONE, config.getKeyMode());
    }

    /**
     * Spec §8.7: idOnTop can be set through resolver.
     */
    @Test
    @DisplayName("ID.16 idOnTop propagated through resolver")
    void idOnTop_propagated() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(Map.of("idOnTop", true))
                .build();

        IdConfig config = resolver.resolveIdConfig(personClass, diagnostics);

        assertTrue(config.isOnTop());
    }

    /**
     * Spec §10/§11: ValueWriter/ValueReader names propagated through resolver.
     */
    @Test
    @DisplayName("ID.17 ValueWriter/ValueReader names propagated through resolver")
    void valueWriterReaderNames_propagated() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(Map.of(
                        "idValueWriterName", "myIdWriter",
                        "idValueReaderName", "myIdReader"
                ))
                .build();

        IdConfig config = resolver.resolveIdConfig(personClass, diagnostics);

        assertEquals("myIdWriter", config.getValueWriterName());
        assertEquals("myIdReader", config.getValueReaderName());
    }
}
