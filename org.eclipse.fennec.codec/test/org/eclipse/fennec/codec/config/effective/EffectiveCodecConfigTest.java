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
package org.eclipse.fennec.codec.config.effective;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.config.IdConfig;
import org.eclipse.fennec.codec.config.TypeConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorService;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.TypeStrategy;
import org.eclipse.fennec.model.metadata.api.MetadataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link EffectiveCodecConfig}.
 */
@DisplayName("EffectiveCodecConfig (Configuration Bridge)")
class EffectiveCodecConfigTest {

    private ConfigurationResolver resolver;
    private DiagnosticCollector diagnostics;
    private MetadataService metadataService;
    private EffectiveCodecConfig config;

    @BeforeEach
    void setUp() {
        resolver = ConfigurationResolver.defaults();
        diagnostics = new DiagnosticCollector();
        metadataService = mock(MetadataService.class);
        config = EffectiveCodecConfig.builder()
                .resolver(resolver)
                .diagnostics(diagnostics)
                .metadataService(metadataService)
                .build();
    }

    @Nested
    @DisplayName("Builder Validation")
    class BuilderValidation {

        @Test
        @DisplayName("requires resolver")
        void requiresResolver() {
            assertThrows(NullPointerException.class, () ->
                    EffectiveCodecConfig.builder()
                            .diagnostics(diagnostics)
                            .build());
        }

        @Test
        @DisplayName("requires diagnostics")
        void requiresDiagnostics() {
            assertThrows(NullPointerException.class, () ->
                    EffectiveCodecConfig.builder()
                            .resolver(resolver)
                            .build());
        }

        @Test
        @DisplayName("allows null metadata service")
        void allowsNullMetadataService() {
            EffectiveCodecConfig cfg = EffectiveCodecConfig.builder()
                    .resolver(resolver)
                    .diagnostics(diagnostics)
                    .build();
            assertNull(cfg.getMetadataService());
        }
    }

    @Nested
    @DisplayName("Config Resolution Delegation")
    class ConfigResolutionDelegation {

        @Test
        @DisplayName("resolves TypeConfig for EClass with defaults")
        void resolvesTypeConfigDefaults() {
            EClass eClass = EcorePackage.eINSTANCE.getEClass();
            TypeConfig typeConfig = config.resolveTypeConfig(eClass);
            assertNotNull(typeConfig);
            assertTrue(typeConfig.isInclude());
            assertEquals(TypeStrategy.URI, typeConfig.getStrategy());
            assertEquals("_type", typeConfig.getTypeKey());
        }

        @Test
        @DisplayName("resolves TypeConfig with custom options")
        void resolvesTypeConfigWithOptions() {
            ConfigurationResolver customResolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("typeStrategy", "NAME"))
                    .build();
            EffectiveCodecConfig customConfig = EffectiveCodecConfig.builder()
                    .resolver(customResolver)
                    .diagnostics(diagnostics)
                    .build();

            EClass eClass = EcorePackage.eINSTANCE.getEClass();
            TypeConfig typeConfig = customConfig.resolveTypeConfig(eClass);
            assertEquals(TypeStrategy.NAME, typeConfig.getStrategy());
        }

        @Test
        @DisplayName("resolves IdConfig for EClass with defaults")
        void resolvesIdConfigDefaults() {
            EClass eClass = EcorePackage.eINSTANCE.getEClass();
            IdConfig idConfig = config.resolveIdConfig(eClass);
            assertNotNull(idConfig);
            assertEquals("_id", idConfig.getKey());
        }

        @Test
        @DisplayName("resolves FeatureConfig for feature with defaults")
        void resolvesFeatureConfigDefaults() {
            EStructuralFeature feature = EcorePackage.eINSTANCE.getEClass_Abstract();
            FeatureConfig featureConfig = config.resolveFeatureConfig(feature);
            assertNotNull(featureConfig);
            assertFalse(featureConfig.isIgnore());
        }

        @Test
        @DisplayName("resolves global TypeConfig")
        void resolvesGlobalTypeConfig() {
            TypeConfig globalType = config.resolveGlobalTypeConfig();
            assertNotNull(globalType);
            assertTrue(globalType.isInclude());
        }
    }

    @Nested
    @DisplayName("MetadataService Integration")
    class MetadataServiceIntegration {

        @Test
        @DisplayName("returns metadata service")
        void returnsMetadataService() {
            assertSame(metadataService, config.getMetadataService());
        }

        @Test
        @DisplayName("resolves EClass by URI via metadata service")
        void resolvesEClassByUri() {
            EClass eClass = EcorePackage.eINSTANCE.getEClass();
            ClassMetadata mockMetadata = mock(ClassMetadata.class);
            when(mockMetadata.getEClass()).thenReturn(eClass);
            when(metadataService.getClassMetadataByURI("http://test/1.0#//MyClass"))
                    .thenReturn(mockMetadata);

            EClass resolved = config.resolveEClassByURI("http://test/1.0#//MyClass");
            assertSame(eClass, resolved);
        }

        @Test
        @DisplayName("returns null for unknown URI")
        void returnsNullForUnknownUri() {
            assertNull(config.resolveEClassByURI("http://unknown/1.0#//Unknown"));
        }

        @Test
        @DisplayName("returns null for null URI")
        void returnsNullForNullUri() {
            assertNull(config.resolveEClassByURI(null));
        }

        @Test
        @DisplayName("returns null for empty URI")
        void returnsNullForEmptyUri() {
            assertNull(config.resolveEClassByURI(""));
        }

        @Test
        @DisplayName("resolves EClass by URI returns null when no metadata service")
        void resolveEClassByUriNoMetadataService() {
            EffectiveCodecConfig noMdConfig = EffectiveCodecConfig.builder()
                    .resolver(resolver)
                    .diagnostics(diagnostics)
                    .build();
            assertNull(noMdConfig.resolveEClassByURI("http://test/1.0#//MyClass"));
        }

        @Test
        @DisplayName("getClassMetadata delegates to metadata service")
        void getClassMetadataDelegates() {
            EClass eClass = EcorePackage.eINSTANCE.getEClass();
            ClassMetadata mockMetadata = mock(ClassMetadata.class);
            when(metadataService.getClassMetadata(eClass)).thenReturn(mockMetadata);

            assertSame(mockMetadata, config.getClassMetadata(eClass));
        }

        @Test
        @DisplayName("getClassMetadata returns null when no metadata service")
        void getClassMetadataReturnsNullWithoutService() {
            EffectiveCodecConfig noMdConfig = EffectiveCodecConfig.builder()
                    .resolver(resolver)
                    .diagnostics(diagnostics)
                    .build();
            assertNull(noMdConfig.getClassMetadata(EcorePackage.eINSTANCE.getEClass()));
        }
    }

    @Nested
    @DisplayName("Global Settings")
    class GlobalSettings {

        @Test
        @DisplayName("default global ignore features is empty")
        void defaultGlobalIgnoreFeatures() {
            assertTrue(config.getGlobalIgnoreFeatures().isEmpty());
        }

        @Test
        @DisplayName("globalIgnoreFeatures is set via builder")
        void globalIgnoreFeaturesFromBuilder() {
            EffectiveCodecConfig cfg = EffectiveCodecConfig.builder()
                    .resolver(resolver)
                    .diagnostics(diagnostics)
                    .globalIgnoreFeatures(List.of("password", "secret"))
                    .build();
            assertEquals(List.of("password", "secret"), cfg.getGlobalIgnoreFeatures());
            assertTrue(cfg.isGloballyIgnored("password"));
            assertFalse(cfg.isGloballyIgnored("name"));
        }

        @Test
        @DisplayName("isGloballyIgnored returns false for null")
        void isGloballyIgnoredNullIsFalse() {
            assertFalse(config.isGloballyIgnored(null));
        }

        @Test
        @DisplayName("default sort alphabetically is false")
        void defaultSortAlphabetically() {
            assertFalse(config.isSortPropertiesAlphabetically());
        }

        @Test
        @DisplayName("default smart compression is false")
        void defaultSmartCompression() {
            assertFalse(config.isSmartCompression());
        }

        @Test
        @DisplayName("smart compression can be enabled")
        void smartCompressionEnabled() {
            EffectiveCodecConfig cfg = EffectiveCodecConfig.builder()
                    .resolver(resolver)
                    .diagnostics(diagnostics)
                    .smartCompression(true)
                    .build();
            assertTrue(cfg.isSmartCompression());
        }

        @Test
        @DisplayName("default useNamesFromExtendedMetaData is false")
        void defaultExtendedMetaData() {
            assertFalse(config.isUseNamesFromExtendedMetaData());
        }
    }

    @Nested
    @DisplayName("Expand Settings")
    class ExpandSettings {

        @Test
        @DisplayName("default expand global is false")
        void defaultExpandGlobal() {
            assertFalse(config.isExpandGlobal());
        }

        @Test
        @DisplayName("default expand depth is 1")
        void defaultExpandDepth() {
            assertEquals(1, config.getExpandDepth());
        }

        @Test
        @DisplayName("default expand ignore bidirectional is true")
        void defaultExpandIgnoreBidirectional() {
            assertTrue(config.isExpandIgnoreBidirectional());
        }

        @Test
        @DisplayName("shouldExpand returns false for null reference")
        void shouldExpandNullIsFalse() {
            assertFalse(config.shouldExpand(null));
        }

        @Test
        @DisplayName("shouldExpand returns true when expandGlobal is true")
        void shouldExpandGlobal() {
            EffectiveCodecConfig cfg = EffectiveCodecConfig.builder()
                    .resolver(resolver)
                    .diagnostics(diagnostics)
                    .expandGlobal(true)
                    .build();
            EReference ref = EcoreFactory.eINSTANCE.createEReference();
            ref.setName("testRef");
            assertTrue(cfg.shouldExpand(ref));
        }

        @Test
        @DisplayName("shouldExpand returns true for named reference")
        void shouldExpandByName() {
            EffectiveCodecConfig cfg = EffectiveCodecConfig.builder()
                    .resolver(resolver)
                    .diagnostics(diagnostics)
                    .expandReferenceNames(Set.of("testRef"))
                    .build();
            EReference ref = EcoreFactory.eINSTANCE.createEReference();
            ref.setName("testRef");
            assertTrue(cfg.shouldExpand(ref));
        }

        @Test
        @DisplayName("shouldExpand returns false for non-matching reference")
        void shouldExpandNonMatching() {
            EReference ref = EcoreFactory.eINSTANCE.createEReference();
            ref.setName("testRef");
            assertFalse(config.shouldExpand(ref));
        }
    }

    @Nested
    @DisplayName("Services")
    class Services {

        @Test
        @DisplayName("returns type discriminator service")
        void returnsTypeDiscriminatorService() {
            TypeDiscriminatorService tds = mock(TypeDiscriminatorService.class);
            EffectiveCodecConfig cfg = EffectiveCodecConfig.builder()
                    .resolver(resolver)
                    .diagnostics(diagnostics)
                    .typeDiscriminatorService(tds)
                    .build();
            assertSame(tds, cfg.getTypeDiscriminatorService());
        }

        @Test
        @DisplayName("returns diagnostics")
        void returnsDiagnostics() {
            assertSame(diagnostics, config.getDiagnostics());
        }

        @Test
        @DisplayName("returns resolver")
        void returnsResolver() {
            assertSame(resolver, config.getResolver());
        }

        @Test
        @DisplayName("returns null value registry when not configured")
        void returnsNullValueRegistry() {
            assertNull(config.getValueRegistry());
        }

        @Test
        @DisplayName("returns null value writer when no registry")
        void returnsNullValueWriterNoRegistry() {
            assertNull(config.getValueWriter("test"));
        }

        @Test
        @DisplayName("returns null value reader when no registry")
        void returnsNullValueReaderNoRegistry() {
            assertNull(config.getValueReader("test"));
        }

        @Test
        @DisplayName("returns null value writer for null name")
        void returnsNullValueWriterNullName() {
            assertNull(config.getValueWriter(null));
        }

        @Test
        @DisplayName("returns null value reader for empty name")
        void returnsNullValueReaderEmptyName() {
            assertNull(config.getValueReader(""));
        }
    }

    @Nested
    @DisplayName("Cache Management")
    class CacheManagement {

        @Test
        @DisplayName("clearCaches does not throw")
        void clearCachesDoesNotThrow() {
            // Populate cache first
            config.resolveTypeConfig(EcorePackage.eINSTANCE.getEClass());
            config.clearCaches();
            // Verify we can still resolve after clearing
            TypeConfig typeConfig = config.resolveTypeConfig(EcorePackage.eINSTANCE.getEClass());
            assertNotNull(typeConfig);
        }
    }
}
