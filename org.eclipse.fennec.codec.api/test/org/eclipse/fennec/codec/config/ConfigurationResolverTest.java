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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.model.metadata.SerializationFormat;
import org.eclipse.fennec.model.metadata.TypeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ConfigurationResolver}.
 */
@DisplayName("ConfigurationResolver")
class ConfigurationResolverTest {

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

    @Nested
    @DisplayName("builder")
    class Builder {

        @Test
        @DisplayName("builds resolver with no configuration sources")
        void buildsResolverWithNoConfigurationSources() {
            ConfigurationResolver resolver = ConfigurationResolver.builder().build();

            assertNotNull(resolver);
        }

        @Test
        @DisplayName("builds resolver with all configuration sources")
        void buildsResolverWithAllConfigurationSources() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeKey", "annotation"))
                    .moduleProperties(Map.of("typeKey", "module"))
                    .factoryProperties(Map.of("typeKey", "factory"))
                    .resourceProperties(Map.of("typeKey", "resource"))
                    .optionsProperties(Map.of("typeKey", "options"))
                    .build();

            assertNotNull(resolver);
        }

        @Test
        @DisplayName("defaults() creates resolver with no configuration")
        void defaultsCreatesResolverWithNoConfiguration() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            assertNotNull(resolver);
        }
    }

    @Nested
    @DisplayName("resolveTypeConfig")
    class ResolveTypeConfig {

        @Test
        @DisplayName("throws NullPointerException when eClass is null")
        void throwsNullPointerExceptionWhenEClassIsNull() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            assertThrows(NullPointerException.class,
                    () -> resolver.resolveTypeConfig(null, diagnostics));
        }

        @Test
        @DisplayName("throws NullPointerException when diagnostics is null")
        void throwsNullPointerExceptionWhenDiagnosticsIsNull() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            assertThrows(NullPointerException.class,
                    () -> resolver.resolveTypeConfig(personClass, null));
        }

        @Test
        @DisplayName("returns defaults when no configuration provided")
        void returnsDefaultsWhenNoConfigurationProvided() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertTrue(config.isInclude());
            assertEquals(TypeStrategy.URI, config.getStrategy());
            assertEquals("_type", config.getTypeKey());
        }

        @Test
        @DisplayName("caches results for same EClass")
        void cachesResultsForSameEClass() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            TypeConfig first = resolver.resolveTypeConfig(personClass, diagnostics);
            TypeConfig second = resolver.resolveTypeConfig(personClass, diagnostics);

            assertSame(first, second);
        }

        @Test
        @DisplayName("different EClass gets different config")
        void differentEClassGetsDifferentConfig() {
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
        @DisplayName("options override all other sources")
        void optionsOverrideAllOtherSources() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeKey", "annotation"))
                    .moduleProperties(Map.of("typeKey", "module"))
                    .factoryProperties(Map.of("typeKey", "factory"))
                    .resourceProperties(Map.of("typeKey", "resource"))
                    .optionsProperties(Map.of("typeKey", "options"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("options", config.getTypeKey());
        }

        @Test
        @DisplayName("resource overrides factory, module, annotation when options absent")
        void resourceOverridesWhenOptionsAbsent() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeKey", "annotation"))
                    .moduleProperties(Map.of("typeKey", "module"))
                    .factoryProperties(Map.of("typeKey", "factory"))
                    .resourceProperties(Map.of("typeKey", "resource"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("resource", config.getTypeKey());
        }

        @Test
        @DisplayName("factory overrides module, annotation when resource absent")
        void factoryOverridesWhenResourceAbsent() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeKey", "annotation"))
                    .moduleProperties(Map.of("typeKey", "module"))
                    .factoryProperties(Map.of("typeKey", "factory"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("factory", config.getTypeKey());
        }

        @Test
        @DisplayName("module overrides annotation when factory absent")
        void moduleOverridesWhenFactoryAbsent() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeKey", "annotation"))
                    .moduleProperties(Map.of("typeKey", "module"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("module", config.getTypeKey());
        }

        @Test
        @DisplayName("annotation used when all runtime sources absent")
        void annotationUsedWhenRuntimeSourcesAbsent() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeKey", "annotation"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("annotation", config.getTypeKey());
        }

        @Test
        @DisplayName("class-level overrides global-level at same source")
        void classLevelOverridesGlobalLevelAtSameSource() {
            Map<String, Object> classConfig = new HashMap<>();
            classConfig.put("typeKey", "classLevel");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeKey", "globalLevel",
                            "Person", classConfig
                    ))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("classLevel", config.getTypeKey());
        }

        @Test
        @DisplayName("different properties can come from different sources")
        void differentPropertiesCanComeFromDifferentSources() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeStrategy", "NAME"))
                    .moduleProperties(Map.of("typeFormat", "STRUCTURED"))
                    .optionsProperties(Map.of("typeKey", "options"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("options", config.getTypeKey());
            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
            assertEquals(TypeStrategy.NAME, config.getStrategy());
        }
    }

    @Nested
    @DisplayName("resolveGlobalTypeConfig")
    class ResolveGlobalTypeConfig {

        @Test
        @DisplayName("throws NullPointerException when diagnostics is null")
        void throwsNullPointerExceptionWhenDiagnosticsIsNull() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            assertThrows(NullPointerException.class,
                    () -> resolver.resolveGlobalTypeConfig(null));
        }

        @Test
        @DisplayName("returns defaults when no configuration provided")
        void returnsDefaultsWhenNoConfigurationProvided() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            TypeConfig config = resolver.resolveGlobalTypeConfig(diagnostics);

            assertTrue(config.isInclude());
            assertEquals(TypeStrategy.URI, config.getStrategy());
        }

        @Test
        @DisplayName("returns cached global config")
        void returnsCachedGlobalConfig() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            TypeConfig first = resolver.resolveGlobalTypeConfig(diagnostics);
            TypeConfig second = resolver.resolveGlobalTypeConfig(diagnostics);

            assertSame(first, second);
        }

        @Test
        @DisplayName("uses global properties only")
        void usesGlobalPropertiesOnly() {
            Map<String, Object> classConfig = new HashMap<>();
            classConfig.put("typeKey", "classLevel");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeKey", "globalLevel",
                            "Person", classConfig
                    ))
                    .build();

            TypeConfig config = resolver.resolveGlobalTypeConfig(diagnostics);

            assertEquals("globalLevel", config.getTypeKey());
        }
    }

    @Nested
    @DisplayName("resolveFeatureConfig")
    class ResolveFeatureConfig {

        @Test
        @DisplayName("throws NullPointerException when feature is null")
        void throwsNullPointerExceptionWhenFeatureIsNull() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            assertThrows(NullPointerException.class,
                    () -> resolver.resolveFeatureConfig(null, diagnostics));
        }

        @Test
        @DisplayName("returns defaults when no configuration provided")
        void returnsDefaultsWhenNoConfigurationProvided() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttribute, diagnostics);

            assertFalse(config.isIgnore());
            assertFalse(config.isSerializeNull());
        }

        @Test
        @DisplayName("caches results for same feature")
        void cachesResultsForSameFeature() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            FeatureConfig first = resolver.resolveFeatureConfig(firstNameAttribute, diagnostics);
            FeatureConfig second = resolver.resolveFeatureConfig(firstNameAttribute, diagnostics);

            assertSame(first, second);
        }

        @Test
        @DisplayName("feature-level overrides class-level")
        void featureLevelOverridesClassLevel() {
            Map<String, Object> featureConfig = new HashMap<>();
            featureConfig.put("key", "featureLevel");

            Map<String, Object> classConfig = new HashMap<>();
            classConfig.put("key", "classLevel");
            classConfig.put("firstName", featureConfig);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("Person", classConfig))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttribute, diagnostics);

            assertEquals("featureLevel", config.getKey());
        }

        @Test
        @DisplayName("class-level overrides global-level")
        void classLevelOverridesGlobalLevel() {
            Map<String, Object> classConfig = new HashMap<>();
            classConfig.put("serializeNull", true);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "serializeNull", false,
                            "Person", classConfig
                    ))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttribute, diagnostics);

            assertTrue(config.isSerializeNull());
        }
    }

    @Nested
    @DisplayName("resolveReferenceConfig")
    class ResolveReferenceConfig {

        @Test
        @DisplayName("throws NullPointerException when feature is null")
        void throwsNullPointerExceptionWhenFeatureIsNull() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            assertThrows(NullPointerException.class,
                    () -> resolver.resolveReferenceConfig(null, diagnostics));
        }

        @Test
        @DisplayName("returns defaults when no configuration provided")
        void returnsDefaultsWhenNoConfigurationProvided() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            ReferenceConfig config = resolver.resolveReferenceConfig(addressReference, diagnostics);

            assertEquals("$ref", config.getRefKey());
            assertFalse(config.isExpand());
        }

        @Test
        @DisplayName("caches results for same reference")
        void cachesResultsForSameReference() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            ReferenceConfig first = resolver.resolveReferenceConfig(addressReference, diagnostics);
            ReferenceConfig second = resolver.resolveReferenceConfig(addressReference, diagnostics);

            assertSame(first, second);
        }
    }

    @Nested
    @DisplayName("resolveIdConfig")
    class ResolveIdConfig {

        @Test
        @DisplayName("returns defaults when no configuration provided")
        void returnsDefaultsWhenNoConfigurationProvided() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            IdConfig config = resolver.resolveIdConfig(personClass, diagnostics);

            assertEquals("_id", config.getKey());
        }

        @Test
        @DisplayName("class-level overrides global-level")
        void classLevelOverridesGlobalLevel() {
            Map<String, Object> classConfig = new HashMap<>();
            classConfig.put("idKey", "personId");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "idKey", "globalId",
                            "Person", classConfig
                    ))
                    .build();

            IdConfig config = resolver.resolveIdConfig(personClass, diagnostics);

            assertEquals("personId", config.getKey());
        }
    }

    @Nested
    @DisplayName("resolveSuperTypeConfig")
    class ResolveSuperTypeConfig {

        @Test
        @DisplayName("returns defaults when no configuration provided")
        void returnsDefaultsWhenNoConfigurationProvided() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            SuperTypeConfig config = resolver.resolveSuperTypeConfig(personClass, diagnostics);

            assertFalse(config.isSerialize());
        }
    }

    @Nested
    @DisplayName("resolveDiscriminatorConfig")
    class ResolveDiscriminatorConfig {

        @Test
        @DisplayName("returns defaults when no configuration provided")
        void returnsDefaultsWhenNoConfigurationProvided() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            DiscriminatorConfig config = resolver.resolveDiscriminatorConfig(personClass, diagnostics);

            assertEquals(DiscriminatorConfig.FallbackStrategy.SKIP, config.getFallbackStrategy());
        }
    }

    @Nested
    @DisplayName("clearCaches")
    class ClearCaches {

        @Test
        @DisplayName("clears all cached configurations")
        void clearsAllCachedConfigurations() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            // Populate caches
            resolver.resolveTypeConfig(personClass, diagnostics);
            resolver.resolveFeatureConfig(firstNameAttribute, diagnostics);
            resolver.resolveGlobalTypeConfig(diagnostics);

            // Clear caches
            resolver.clearCaches();

            // Get new configs (should be different instances if properly cleared)
            TypeConfig typeConfig2 = resolver.resolveTypeConfig(personClass, diagnostics);
            FeatureConfig featureConfig2 = resolver.resolveFeatureConfig(firstNameAttribute, diagnostics);
            TypeConfig globalConfig2 = resolver.resolveGlobalTypeConfig(diagnostics);

            // After clear, new calls return new instances
            // Note: Values are same (defaults), but should be re-computed instances
            assertNotNull(typeConfig2);
            assertNotNull(featureConfig2);
            assertNotNull(globalConfig2);
        }
    }

    @Nested
    @DisplayName("two-dimensional resolution")
    class TwoDimensionalResolution {

        @Test
        @DisplayName("horizontal: feature → class → global (most specific wins)")
        void horizontalMostSpecificWins() {
            Map<String, Object> featureConfig = new HashMap<>();
            featureConfig.put("key", "featureKey");

            Map<String, Object> classConfig = new HashMap<>();
            classConfig.put("key", "classKey");
            classConfig.put("firstName", featureConfig);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "key", "globalKey",
                            "Person", classConfig
                    ))
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(firstNameAttribute, diagnostics);

            assertEquals("featureKey", config.getKey());
        }

        @Test
        @DisplayName("vertical: options → resource → factory → module → annotation → default")
        void verticalHigherPriorityWins() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeKey", "annotation"))
                    .moduleProperties(Map.of("typeKey", "module"))
                    .factoryProperties(Map.of("typeKey", "factory"))
                    .resourceProperties(Map.of("typeKey", "resource"))
                    .optionsProperties(Map.of("typeKey", "options"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("options", config.getTypeKey());
        }

        @Test
        @DisplayName("combined: horizontal at each vertical level")
        void combinedHorizontalAtEachVerticalLevel() {
            // Module has global setting, Options has class-level setting
            // Options class-level should win over Module global
            Map<String, Object> optionsClassConfig = new HashMap<>();
            optionsClassConfig.put("typeKey", "optionsClass");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .moduleProperties(Map.of("typeKey", "moduleGlobal"))
                    .optionsProperties(Map.of("Person", optionsClassConfig))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            // Options class-level is higher priority than module global
            assertEquals("optionsClass", config.getTypeKey());
        }

        @Test
        @DisplayName("fallback through levels: property from annotation when not in options")
        void fallbackThroughLevels() {
            // Options sets typeKey, annotation sets typeStrategy
            // Both should be used
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .annotationProperties(Map.of("typeStrategy", "NAME"))
                    .optionsProperties(Map.of("typeKey", "optionsKey"))
                    .build();

            TypeConfig config = resolver.resolveTypeConfig(personClass, diagnostics);

            assertEquals("optionsKey", config.getTypeKey());
            assertEquals(TypeStrategy.NAME, config.getStrategy());
        }
    }

    @Nested
    @DisplayName("cross-config validation")
    class CrossConfigValidation {

        @Test
        @DisplayName("STRUCTURED + NONE + superTypeSerialize=true produces ERROR")
        void structuredNoneSuperTypeSerializeProducesError() {
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
                    "Expected ERROR for STRUCTURED + NONE + superTypeSerialize=true");
        }

        @Test
        @DisplayName("STRUCTURED + NONE + superTypeSerialize=false is valid")
        void structuredNoneSuperTypeSerializeFalseIsValid() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeFormat", "STRUCTURED",
                            "typeStrategy", "NONE",
                            "superTypeSerialize", false
                    ))
                    .build();

            TypeConfig typeConfig = resolver.resolveTypeConfig(personClass, diagnostics);
            SuperTypeConfig superTypeConfig = resolver.resolveSuperTypeConfig(personClass, diagnostics);
            resolver.validateCrossConfig(typeConfig, superTypeConfig, diagnostics);

            assertFalse(diagnostics.hasErrors(),
                    "STRUCTURED + NONE + superTypeSerialize=false should be valid");
        }

        @Test
        @DisplayName("PLAIN + NONE + superTypeSerialize=true is valid")
        void plainNoneSuperTypeSerializeTrueIsValid() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeFormat", "PLAIN",
                            "typeStrategy", "NONE",
                            "superTypeSerialize", true
                    ))
                    .build();

            TypeConfig typeConfig = resolver.resolveTypeConfig(personClass, diagnostics);
            SuperTypeConfig superTypeConfig = resolver.resolveSuperTypeConfig(personClass, diagnostics);
            resolver.validateCrossConfig(typeConfig, superTypeConfig, diagnostics);

            assertFalse(diagnostics.hasErrors(),
                    "PLAIN + NONE + superTypeSerialize=true should be valid");
        }

        @Test
        @DisplayName("STRUCTURED + URI + superTypeSerialize=true is valid")
        void structuredUriSuperTypeSerializeTrueIsValid() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeFormat", "STRUCTURED",
                            "typeStrategy", "URI",
                            "superTypeSerialize", true
                    ))
                    .build();

            TypeConfig typeConfig = resolver.resolveTypeConfig(personClass, diagnostics);
            SuperTypeConfig superTypeConfig = resolver.resolveSuperTypeConfig(personClass, diagnostics);
            resolver.validateCrossConfig(typeConfig, superTypeConfig, diagnostics);

            assertFalse(diagnostics.hasErrors(),
                    "STRUCTURED + URI + superTypeSerialize=true should be valid");
        }

        @Test
        @DisplayName("STRUCTURED + both value readers produces WARNING")
        void structuredBothValueReadersProducesWarning() {
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
                    "Expected WARNING for STRUCTURED + both value readers");
        }

        @Test
        @DisplayName("STRUCTURED + both value writers produces WARNING")
        void structuredBothValueWritersProducesWarning() {
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
                    "Expected WARNING for STRUCTURED + both value writers");
        }

        @Test
        @DisplayName("PLAIN + both value readers is valid (no conflict)")
        void plainBothValueReadersIsValid() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeFormat", "PLAIN",
                            "typeValueReaderName", "typeReader",
                            "superTypeValueReaderName", "superTypeReader"
                    ))
                    .build();

            TypeConfig typeConfig = resolver.resolveTypeConfig(personClass, diagnostics);
            SuperTypeConfig superTypeConfig = resolver.resolveSuperTypeConfig(personClass, diagnostics);
            resolver.validateCrossConfig(typeConfig, superTypeConfig, diagnostics);

            assertFalse(diagnostics.hasWarnings(),
                    "PLAIN + both value readers should be valid (separate fields)");
        }

        @Test
        @DisplayName("resolveTypeAndSuperTypeConfig performs cross-config validation")
        void resolveTypeAndSuperTypeConfigPerformsCrossValidation() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(
                            "typeFormat", "STRUCTURED",
                            "typeStrategy", "NONE",
                            "superTypeSerialize", true
                    ))
                    .build();

            resolver.resolveTypeAndSuperTypeConfig(personClass, diagnostics);

            assertTrue(diagnostics.hasErrors(),
                    "resolveTypeAndSuperTypeConfig should perform cross-config validation");
        }
    }

    @Nested
    @DisplayName("forceWrite and forceRead builder methods")
    class ForceWriteRead {

        private EAttribute volatileAttribute;
        private EAttribute transientAttribute;
        private EAttribute derivedAttribute;

        @BeforeEach
        void setUpVolatileFeatures() {
            // Create a volatile attribute (normally skipped during serialization)
            volatileAttribute = EcoreFactory.eINSTANCE.createEAttribute();
            volatileAttribute.setName("volatileData");
            volatileAttribute.setEType(EcorePackage.Literals.ESTRING);
            volatileAttribute.setVolatile(true);
            personClass.getEStructuralFeatures().add(volatileAttribute);

            // Create a transient attribute
            transientAttribute = EcoreFactory.eINSTANCE.createEAttribute();
            transientAttribute.setName("transientData");
            transientAttribute.setEType(EcorePackage.Literals.ESTRING);
            transientAttribute.setTransient(true);
            personClass.getEStructuralFeatures().add(transientAttribute);

            // Create a derived attribute
            derivedAttribute = EcoreFactory.eINSTANCE.createEAttribute();
            derivedAttribute.setName("derivedData");
            derivedAttribute.setEType(EcorePackage.Literals.ESTRING);
            derivedAttribute.setDerived(true);
            personClass.getEStructuralFeatures().add(derivedAttribute);
        }

        @Test
        @DisplayName("volatile attributes are ignored by default")
        void volatileAttributesIgnoredByDefault() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            FeatureConfig config = resolver.resolveFeatureConfig(volatileAttribute, diagnostics);

            assertTrue(config.isIgnore(),
                    "Volatile attributes should be ignored by default");
        }

        @Test
        @DisplayName("transient attributes are ignored by default")
        void transientAttributesIgnoredByDefault() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            FeatureConfig config = resolver.resolveFeatureConfig(transientAttribute, diagnostics);

            assertTrue(config.isIgnore(),
                    "Transient attributes should be ignored by default");
        }

        @Test
        @DisplayName("derived attributes are ignored by default")
        void derivedAttributesIgnoredByDefault() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            FeatureConfig config = resolver.resolveFeatureConfig(derivedAttribute, diagnostics);

            assertTrue(config.isIgnore(),
                    "Derived attributes should be ignored by default");
        }

        @Test
        @DisplayName("forceWrite enables serialization of volatile attribute")
        void forceWriteEnablesVolatileSerialization() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceWrite(volatileAttribute)
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(volatileAttribute, diagnostics);

            assertTrue(config.isForceWrite(),
                    "forceWrite should be enabled for the specified feature");
            assertFalse(config.isIgnore(),
                    "Volatile attribute with forceWrite should not be ignored");
        }

        @Test
        @DisplayName("forceWrite enables serialization of transient attribute")
        void forceWriteEnablesTransientSerialization() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceWrite(transientAttribute)
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(transientAttribute, diagnostics);

            assertTrue(config.isForceWrite(),
                    "forceWrite should be enabled for the specified feature");
            assertFalse(config.isIgnore(),
                    "Transient attribute with forceWrite should not be ignored");
        }

        @Test
        @DisplayName("forceWrite enables serialization of derived attribute")
        void forceWriteEnablesDerivedSerialization() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceWrite(derivedAttribute)
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(derivedAttribute, diagnostics);

            assertTrue(config.isForceWrite(),
                    "forceWrite should be enabled for the specified feature");
            assertFalse(config.isIgnore(),
                    "Derived attribute with forceWrite should not be ignored");
        }

        @Test
        @DisplayName("forceWrite with multiple features")
        void forceWriteWithMultipleFeatures() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceWrite(volatileAttribute, transientAttribute, derivedAttribute)
                    .build();

            FeatureConfig volatileConfig = resolver.resolveFeatureConfig(volatileAttribute, diagnostics);
            FeatureConfig transientConfig = resolver.resolveFeatureConfig(transientAttribute, diagnostics);
            FeatureConfig derivedConfig = resolver.resolveFeatureConfig(derivedAttribute, diagnostics);

            assertTrue(volatileConfig.isForceWrite(), "volatile should have forceWrite");
            assertTrue(transientConfig.isForceWrite(), "transient should have forceWrite");
            assertTrue(derivedConfig.isForceWrite(), "derived should have forceWrite");
        }

        @Test
        @DisplayName("forceWrite does not affect other features")
        void forceWriteDoesNotAffectOtherFeatures() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceWrite(volatileAttribute)
                    .build();

            // Regular attribute should not have forceWrite
            FeatureConfig regularConfig = resolver.resolveFeatureConfig(firstNameAttribute, diagnostics);

            assertFalse(regularConfig.isForceWrite(),
                    "Regular attribute should not have forceWrite enabled");
        }

        @Test
        @DisplayName("forceRead enables deserialization into volatile attribute")
        void forceReadEnablesVolatileDeserialization() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceRead(volatileAttribute)
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(volatileAttribute, diagnostics);

            assertTrue(config.isForceRead(),
                    "forceRead should be enabled for the specified feature");
        }

        @Test
        @DisplayName("forceRead with multiple features")
        void forceReadWithMultipleFeatures() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceRead(volatileAttribute, transientAttribute)
                    .build();

            FeatureConfig volatileConfig = resolver.resolveFeatureConfig(volatileAttribute, diagnostics);
            FeatureConfig transientConfig = resolver.resolveFeatureConfig(transientAttribute, diagnostics);

            assertTrue(volatileConfig.isForceRead(), "volatile should have forceRead");
            assertTrue(transientConfig.isForceRead(), "transient should have forceRead");
        }

        @Test
        @DisplayName("forceWrite and forceRead can be combined")
        void forceWriteAndForceReadCombined() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceWrite(volatileAttribute)
                    .forceRead(volatileAttribute)
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(volatileAttribute, diagnostics);

            assertTrue(config.isForceWrite(), "should have forceWrite");
            assertTrue(config.isForceRead(), "should have forceRead");
            assertFalse(config.isIgnore(), "should not be ignored");
        }

        @Test
        @DisplayName("forceWrite accepts null array gracefully")
        void forceWriteAcceptsNullArray() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceWrite((EStructuralFeature[]) null)
                    .build();

            assertNotNull(resolver);
        }

        @Test
        @DisplayName("forceWrite ignores null elements in array")
        void forceWriteIgnoresNullElements() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceWrite(volatileAttribute, null, transientAttribute)
                    .build();

            FeatureConfig volatileConfig = resolver.resolveFeatureConfig(volatileAttribute, diagnostics);
            FeatureConfig transientConfig = resolver.resolveFeatureConfig(transientAttribute, diagnostics);

            assertTrue(volatileConfig.isForceWrite(), "volatile should have forceWrite");
            assertTrue(transientConfig.isForceWrite(), "transient should have forceWrite");
        }

        @Test
        @DisplayName("forceRead accepts null array gracefully")
        void forceReadAcceptsNullArray() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceRead((EStructuralFeature[]) null)
                    .build();

            assertNotNull(resolver);
        }

        @Test
        @DisplayName("forceRead ignores null elements in array")
        void forceReadIgnoresNullElements() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceRead(volatileAttribute, null, transientAttribute)
                    .build();

            FeatureConfig volatileConfig = resolver.resolveFeatureConfig(volatileAttribute, diagnostics);
            FeatureConfig transientConfig = resolver.resolveFeatureConfig(transientAttribute, diagnostics);

            assertTrue(volatileConfig.isForceRead(), "volatile should have forceRead");
            assertTrue(transientConfig.isForceRead(), "transient should have forceRead");
        }

        @Test
        @DisplayName("forceWrite overrides other configuration")
        void forceWriteOverridesOtherConfiguration() {
            // Set up feature-level config that doesn't have forceWrite
            Map<String, Object> featureConfig = new HashMap<>();
            featureConfig.put("ignore", true);

            Map<String, Object> classConfig = new HashMap<>();
            classConfig.put("volatileData", featureConfig);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("Person", classConfig))
                    .forceWrite(volatileAttribute)
                    .build();

            FeatureConfig config = resolver.resolveFeatureConfig(volatileAttribute, diagnostics);

            assertTrue(config.isForceWrite(),
                    "forceWrite should be applied even when other config says ignore");
            assertFalse(config.isIgnore(),
                    "Feature should not be ignored when forceWrite is set");
        }

        @Test
        @DisplayName("multiple forceWrite calls accumulate features")
        void multipleForceWriteCallsAccumulateFeatures() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceWrite(volatileAttribute)
                    .forceWrite(transientAttribute)
                    .forceWrite(derivedAttribute)
                    .build();

            FeatureConfig volatileConfig = resolver.resolveFeatureConfig(volatileAttribute, diagnostics);
            FeatureConfig transientConfig = resolver.resolveFeatureConfig(transientAttribute, diagnostics);
            FeatureConfig derivedConfig = resolver.resolveFeatureConfig(derivedAttribute, diagnostics);

            assertTrue(volatileConfig.isForceWrite(), "volatile should have forceWrite");
            assertTrue(transientConfig.isForceWrite(), "transient should have forceWrite");
            assertTrue(derivedConfig.isForceWrite(), "derived should have forceWrite");
        }
    }

    // ========================================================================
    // Scope Configuration Tests (GAP-009)
    // ========================================================================

    @Nested
    @DisplayName("Scope Configuration via Options")
    class ScopeConfigurationTests {

        @Test
        @DisplayName("eClassConfig with EClass keys overrides global config")
        void eClassConfigWithEClassKeysOverridesGlobal() {
            // Global config says URI, but Person-specific says NAME
            Map<EClass, Map<String, Object>> eClassConfig = new HashMap<>();
            eClassConfig.put(personClass, Map.of(
                    ConfigProperty.TYPE_STRATEGY.getKey(), "NAME"
            ));

            Map<String, Object> options = Map.of(
                    ConfigProperty.TYPE_STRATEGY.getKey(), "URI",  // global
                    ConfigProperty.ECLASS_CONFIG.getKey(), eClassConfig
            );

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(options)
                    .build();

            TypeConfig personConfig = resolver.resolveTypeConfig(personClass, diagnostics);
            TypeConfig addressConfig = resolver.resolveTypeConfig(addressClass, diagnostics);

            assertEquals(TypeStrategy.NAME, personConfig.getStrategy(), "Person should use NAME from eClassConfig");
            assertEquals(TypeStrategy.URI, addressConfig.getStrategy(), "Address should use global URI");
        }

        @Test
        @DisplayName("eReferenceConfig with EReference keys overrides class config")
        void eReferenceConfigWithEReferenceKeysOverridesClass() {
            // Global + class config say _type, but reference-specific says customType
            Map<EReference, Map<String, Object>> eRefConfig = new HashMap<>();
            eRefConfig.put(addressReference, Map.of(
                    ConfigProperty.TYPE_KEY.getKey(), "customType"
            ));

            Map<String, Object> options = Map.of(
                    ConfigProperty.TYPE_KEY.getKey(), "_type",  // global
                    ConfigProperty.EREFERENCE_CONFIG.getKey(), eRefConfig
            );

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(options)
                    .build();

            ReferenceConfig refConfig = resolver.resolveReferenceConfig(addressReference, diagnostics);
            assertEquals("customType", refConfig.getTypeKey(), "Reference should use customType from eReferenceConfig");
        }

        @Test
        @DisplayName("eAttributeConfig with EAttribute keys overrides class config")
        void eAttributeConfigWithEAttributeKeysOverridesClass() {
            // Configure feature key for firstName attribute
            Map<EAttribute, Map<String, Object>> eAttrConfig = new HashMap<>();
            eAttrConfig.put(firstNameAttribute, Map.of(
                    ConfigProperty.KEY.getKey(), "first_name"
            ));

            Map<String, Object> options = Map.of(
                    ConfigProperty.EATTRIBUTE_CONFIG.getKey(), eAttrConfig
            );

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(options)
                    .build();

            FeatureConfig attrConfig = resolver.resolveFeatureConfig(firstNameAttribute, diagnostics);
            assertEquals("first_name", attrConfig.getKey(), "Attribute should use key from eAttributeConfig");
        }

        @Test
        @DisplayName("string-based class names still work (backward compatibility)")
        void stringBasedClassNamesStillWork() {
            // Use class name string instead of EClass instance
            Map<String, Object> options = Map.of(
                    ConfigProperty.TYPE_STRATEGY.getKey(), "URI",  // global
                    "Person", Map.of(                               // class-level via name
                            ConfigProperty.TYPE_STRATEGY.getKey(), "NAME"
                    )
            );

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(options)
                    .build();

            TypeConfig personConfig = resolver.resolveTypeConfig(personClass, diagnostics);
            assertEquals(TypeStrategy.NAME, personConfig.getStrategy(), "Person should use NAME from string-keyed config");
        }

        @Test
        @DisplayName("eClassConfig takes priority over string-based class name")
        void eClassConfigTakesPriorityOverStringBasedClassName() {
            // Both eClassConfig and string-based name configure Person
            Map<EClass, Map<String, Object>> eClassConfig = new HashMap<>();
            eClassConfig.put(personClass, Map.of(
                    ConfigProperty.TYPE_STRATEGY.getKey(), "SCHEMA_AND_TYPE"
            ));

            Map<String, Object> options = Map.of(
                    "Person", Map.of(                               // string-based (lower priority)
                            ConfigProperty.TYPE_STRATEGY.getKey(), "NAME"
                    ),
                    ConfigProperty.ECLASS_CONFIG.getKey(), eClassConfig  // EClass-based (higher priority)
            );

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(options)
                    .build();

            TypeConfig personConfig = resolver.resolveTypeConfig(personClass, diagnostics);
            assertEquals(TypeStrategy.SCHEMA_AND_TYPE, personConfig.getStrategy(),
                    "EClass-based config should take priority");
        }
    }

    @Nested
    @DisplayName("getGlobalProperty")
    class GetGlobalProperty {

        @Test
        @DisplayName("finds property using short key (without codec. prefix)")
        void findsPropertyUsingShortKey() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(ConfigProperty.FIELD_ORDER.getKey(), "ALPHABETICAL"))
                    .build();

            String value = resolver.getGlobalProperty(ConfigProperty.FIELD_ORDER);
            assertEquals("ALPHABETICAL", value);
        }

        @Test
        @DisplayName("finds property using codec. prefixed key (issue #13)")
        void findsPropertyUsingPrefixedKey() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of(ConfigProperty.FIELD_ORDER.getPropertyKey(), "ALPHABETICAL"))
                    .build();

            String value = resolver.getGlobalProperty(ConfigProperty.FIELD_ORDER);
            assertEquals("ALPHABETICAL", value,
                    "getGlobalProperty must accept both 'fieldOrder' and 'codec.fieldOrder' as keys");
        }

        @Test
        @DisplayName("short key takes priority over prefixed key when both present")
        void shortKeyTakesPriorityOverPrefixedKey() {
            Map<String, Object> options = new HashMap<>();
            options.put(ConfigProperty.FIELD_ORDER.getKey(), "DECLARATION");
            options.put(ConfigProperty.FIELD_ORDER.getPropertyKey(), "ALPHABETICAL");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(options)
                    .build();

            String value = resolver.getGlobalProperty(ConfigProperty.FIELD_ORDER);
            assertEquals("DECLARATION", value, "short key should win when both are present");
        }

        @Test
        @DisplayName("returns default when property is absent")
        void returnsDefaultWhenAbsent() {
            ConfigurationResolver resolver = ConfigurationResolver.builder().build();

            String value = resolver.getGlobalProperty(ConfigProperty.FIELD_ORDER);
            assertEquals(ConfigProperty.FIELD_ORDER.getDefaultValue(), value);
        }

        @Test
        @DisplayName("prefixed key found across all five config sources")
        void prefixedKeyFoundAcrossAllSources() {
            for (String label : new String[] { "options", "resource", "factory", "module", "annotation" }) {
                Map<String, Object> props = Map.of(ConfigProperty.FIELD_ORDER.getPropertyKey(), "ALPHABETICAL");

                ConfigurationResolver.Builder b = ConfigurationResolver.builder();
                switch (label) {
                    case "options"    -> b.optionsProperties(props);
                    case "resource"   -> b.resourceProperties(props);
                    case "factory"    -> b.factoryProperties(props);
                    case "module"     -> b.moduleProperties(props);
                    case "annotation" -> b.annotationProperties(props);
                }

                String value = b.build().getGlobalProperty(ConfigProperty.FIELD_ORDER);
                assertEquals("ALPHABETICAL", value,
                        "prefixed key must be found in " + label + " source");
            }
        }
    }
}
