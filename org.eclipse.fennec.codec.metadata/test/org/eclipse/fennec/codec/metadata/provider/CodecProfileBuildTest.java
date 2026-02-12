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
package org.eclipse.fennec.codec.metadata.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile;
import org.eclipse.fennec.codec.metadata.model.codec.CodecFactory;
import org.eclipse.fennec.codec.metadata.model.codec.CodecPackageProfile;
import org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig;
import org.eclipse.fennec.model.metadata.ClassAspect;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.ClassProfile;
import org.eclipse.fennec.model.metadata.FeatureAspect;
import org.eclipse.fennec.model.metadata.FeatureMetadata;
import org.eclipse.fennec.model.metadata.EnumSerializationStrategy;
import org.eclipse.fennec.model.metadata.IdKeyMode;
import org.eclipse.fennec.model.metadata.IdStrategy;
import org.eclipse.fennec.model.metadata.PackageMetadata;
import org.eclipse.fennec.model.metadata.PackageProfile;
import org.eclipse.fennec.model.metadata.SerializationFormat;
import org.eclipse.fennec.model.metadata.SuperTypeSelection;
import org.eclipse.fennec.model.metadata.TypeStrategy;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.service.MetadataServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for buildProfiles parameter immutability and profile retrieval.
 * <p>
 * These tests live in codec.metadata (not model.metadata) because they require
 * concrete EMF-registered aspect types (ClassCodecAspect, FeatureCodecAspect)
 * for EcoreUtil.copy to work in buildProfilesForProvider.
 * </p>
 */
class CodecProfileBuildTest {

    private MetadataWhiteboard service;
    private EPackage testPackage;
    private EClass personClass;
    private EClass addressClass;
    private EAttribute nameAttr;

    @SuppressWarnings("restriction")
	@BeforeEach
    void setUp() {
        service = new MetadataServiceImpl();
        createTestPackage();
    }

    private void createTestPackage() {
        testPackage = EcoreFactory.eINSTANCE.createEPackage();
        testPackage.setName("test");
        testPackage.setNsURI("http://test.example.org/profile/1.0");
        testPackage.setNsPrefix("test");

        personClass = EcoreFactory.eINSTANCE.createEClass();
        personClass.setName("Person");
        testPackage.getEClassifiers().add(personClass);

        nameAttr = EcoreFactory.eINSTANCE.createEAttribute();
        nameAttr.setName("name");
        nameAttr.setEType(EcorePackage.Literals.ESTRING);
        personClass.getEStructuralFeatures().add(nameAttr);

        addressClass = EcoreFactory.eINSTANCE.createEClass();
        addressClass.setName("Address");
        testPackage.getEClassifiers().add(addressClass);

        EReference addressRef = EcoreFactory.eINSTANCE.createEReference();
        addressRef.setName("address");
        addressRef.setEType(addressClass);
        addressRef.setContainment(true);
        personClass.getEStructuralFeatures().add(addressRef);
    }

    // ========================================================================
    // buildProfiles Parameter Immutability Tests
    // ========================================================================

    @Test
    void testBuildProfilesReceivesCopy() {
        ProfileCapturingProvider provider = new ProfileCapturingProvider();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        PackageMetadata received = provider.receivedMetadataCopy;
        assertNotNull(received, "buildProfiles should have been called");

        PackageMetadata original = service.getPackageMetadata("http://test.example.org/profile/1.0");
        assertNotSame(original, received,
                "buildProfiles should receive a copy, not the original PackageMetadata");
    }

    @Test
    void testBuildProfilesCopyMutationDoesNotAffectOriginal() {
        ProfileCapturingProvider provider = new ProfileCapturingProvider();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        PackageMetadata received = provider.receivedMetadataCopy;
        assertNotNull(received);
        assertTrue(received.getClasses().size() > 0, "Copy should have classes");

        // Mutate the copy
        received.getClasses().clear();

        // Original should be unaffected
        PackageMetadata original = service.getPackageMetadata("http://test.example.org/profile/1.0");
        assertEquals(2, original.getClasses().size(),
                "Clearing the copy's classes must not affect the original PackageMetadata");
    }

    @Test
    void testBuildProfilesCopyPreservesStructure() {
        ProfileCapturingProvider provider = new ProfileCapturingProvider();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        PackageMetadata received = provider.receivedMetadataCopy;
        assertNotNull(received);

        // Structure should match the original
        assertEquals("http://test.example.org/profile/1.0", received.getNsURI());
        assertSame(testPackage, received.getEPackage());
        assertEquals(2, received.getClasses().size());

        // Class metadata should reference the same EClasses
        boolean foundPerson = false;
        boolean foundAddress = false;
        for (ClassMetadata classMeta : received.getClasses()) {
            if (classMeta.getEClass() == personClass) {
                foundPerson = true;
                assertEquals("Person", classMeta.getName());
            } else if (classMeta.getEClass() == addressClass) {
                foundAddress = true;
                assertEquals("Address", classMeta.getName());
            }
        }
        assertTrue(foundPerson, "Copy should contain Person class metadata");
        assertTrue(foundAddress, "Copy should contain Address class metadata");
    }

    @Test
    void testBuildProfilesCopyIsFiltered() {
        // Register two providers — the CodecAspectProvider and a capturing one
        CodecAspectProvider codecProvider = new CodecAspectProvider();
        ProfileCapturingProvider capturingProvider = new ProfileCapturingProvider();
        service.registerAspectProvider(codecProvider);
        service.registerAspectProvider(capturingProvider);
        service.registerPackage(testPackage);

        PackageMetadata received = capturingProvider.receivedMetadataCopy;
        assertNotNull(received);

        // The filtered copy should only contain aspects from the capturing provider
        for (ClassMetadata classMeta : received.getClasses()) {
            for (ClassAspect aspect : classMeta.getAspects()) {
                assertEquals("test-profile", aspect.getTypeId(),
                        "Filtered copy should only contain aspects from this provider");
            }
            for (FeatureMetadata featureMeta : classMeta.getFeatures()) {
                for (FeatureAspect aspect : featureMeta.getAspects()) {
                    assertEquals("test-profile", aspect.getTypeId(),
                            "Filtered copy should only contain aspects from this provider");
                }
            }
        }
    }

    // ========================================================================
    // Profile Retrieval Tests
    // ========================================================================

    @Test
    void testGetPackageProfile() {
        ProfileCapturingProvider provider = new ProfileCapturingProvider();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        PackageProfile profile = service.getPackageProfile(testPackage, "test-profile");
        assertNotNull(profile, "Should find PackageProfile by EPackage and typeId");
        assertEquals("test-profile", profile.getTypeId());
        assertTrue(profile instanceof CodecPackageProfile);
    }

    @Test
    void testGetPackageProfileByNsURI() {
        ProfileCapturingProvider provider = new ProfileCapturingProvider();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        PackageProfile profile = service.getPackageProfileByNsURI(
                "http://test.example.org/profile/1.0", "test-profile");
        assertNotNull(profile, "Should find PackageProfile by nsURI and typeId");
        assertEquals("test-profile", profile.getTypeId());
    }

    @Test
    void testGetPackageProfileNotFound() {
        ProfileCapturingProvider provider = new ProfileCapturingProvider();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        PackageProfile profile = service.getPackageProfile(testPackage, "nonexistent");
        assertNull(profile, "Should return null for unknown typeId");
    }

    @Test
    void testGetPackageProfileNullPackage() {
        PackageProfile profile = service.getPackageProfile(null, "test-profile");
        assertNull(profile);
    }

    @Test
    void testGetClassProfile() {
        ProfileCapturingProvider provider = new ProfileCapturingProvider();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        ClassProfile profile = service.getClassProfile(personClass, "test-profile");
        assertNotNull(profile, "Should find ClassProfile by EClass and typeId");
        assertTrue(profile instanceof CodecClassProfile);
        assertSame(personClass, profile.getEClass());
    }

    @Test
    void testGetClassProfileByURI() {
        ProfileCapturingProvider provider = new ProfileCapturingProvider();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        ClassMetadata personMeta = service.getClassMetadata(personClass);
        String uri = personMeta.getTypeURI();

        ClassProfile profile = service.getClassProfileByURI(uri, "test-profile");
        assertNotNull(profile, "Should find ClassProfile by URI and typeId");
        assertSame(personClass, profile.getEClass());
    }

    @Test
    void testGetClassProfileNotFound() {
        ProfileCapturingProvider provider = new ProfileCapturingProvider();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        ClassProfile profile = service.getClassProfile(personClass, "nonexistent");
        assertNull(profile, "Should return null for unknown typeId");
    }

    @Test
    void testGetClassProfileNullClass() {
        ClassProfile profile = service.getClassProfile(null, "test-profile");
        assertNull(profile);
    }

    @Test
    void testProfileContainedInPackageMetadata() {
        ProfileCapturingProvider provider = new ProfileCapturingProvider();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        PackageMetadata pkgMeta = service.getPackageMetadata("http://test.example.org/profile/1.0");
        assertFalse(pkgMeta.getProfiles().isEmpty(),
                "PackageMetadata should contain profiles after provider builds them");

        PackageProfile profile = pkgMeta.getProfiles().stream()
                .filter(p -> "test-profile".equals(p.getTypeId()))
                .findFirst()
                .orElse(null);
        assertNotNull(profile, "Profile should be contained in PackageMetadata.profiles");
    }

    @Test
    void testClassProfilesContainedInPackageProfile() {
        ProfileCapturingProvider provider = new ProfileCapturingProvider();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        PackageProfile pkgProfile = service.getPackageProfile(testPackage, "test-profile");
        assertNotNull(pkgProfile);

        assertEquals(2, pkgProfile.getClassProfiles().size(),
                "PackageProfile should have one ClassProfile per EClass");
    }

    @Test
    void testProfileRemovedOnProviderUnregister() {
        ProfileCapturingProvider provider = new ProfileCapturingProvider();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        // Verify profile exists
        assertNotNull(service.getPackageProfile(testPackage, "test-profile"));

        // Unregister provider
        service.unregisterAspectProvider(provider);

        // Profile should be removed
        assertNull(service.getPackageProfile(testPackage, "test-profile"));
    }

    // ========================================================================
    // Pre-Merged Profile State Tests — Default Profiles (No Annotations)
    // ========================================================================

    @Nested
    @DisplayName("Default Profile (No Annotations)")
    class DefaultProfileTests {

        private CodecClassProfile getPersonProfile() {
            CodecAspectProvider codecProvider = new CodecAspectProvider();
            service.registerAspectProvider(codecProvider);
            service.registerPackage(testPackage);

            PackageProfile pkgProfile = service.getPackageProfile(testPackage, "codec");
            assertNotNull(pkgProfile, "Codec package profile should exist");

            ClassProfile classProfile = service.getClassProfile(personClass, "codec");
            assertNotNull(classProfile, "Person class profile should exist");
            assertTrue(classProfile instanceof CodecClassProfile);
            return (CodecClassProfile) classProfile;
        }

        @Test
        @DisplayName("default TypeConfig has EMF defaults")
        void testDefaultTypeConfig() {
            CodecClassProfile profile = getPersonProfile();

            TypeSerializationConfig typeConfig = profile.getTypeConfig();
            assertNotNull(typeConfig, "TypeConfig should always be present (with defaults)");
            assertEquals(TypeStrategy.URI, typeConfig.getStrategy());
            assertEquals(SerializationFormat.PLAIN, typeConfig.getFormat());
            assertEquals("_type", typeConfig.getTypeKey());
            assertEquals("schema", typeConfig.getSchemaKey());
            assertEquals("name", typeConfig.getNameKey());
        }

        @Test
        @DisplayName("default IdConfig has EMF defaults")
        void testDefaultIdConfig() {
            CodecClassProfile profile = getPersonProfile();

            IdSerializationConfig idConfig = profile.getIdConfig();
            assertNotNull(idConfig, "IdConfig should always be present (with defaults)");
            assertEquals(IdStrategy.ID_FIELD, idConfig.getStrategy());
            assertEquals(SerializationFormat.PLAIN, idConfig.getFormat());
            assertEquals("_id", idConfig.getIdKey());
            assertEquals("-", idConfig.getSeparator());
            assertEquals(IdKeyMode.ID_ONLY, idConfig.getKeyMode());
            assertTrue(idConfig.isOnTop());
            assertTrue(idConfig.isSerializeSeparator());
            assertEquals("separator", idConfig.getSeparatorKey());
            assertEquals("id", idConfig.getValueKey());
        }

        @Test
        @DisplayName("default SuperTypeConfig has EMF defaults")
        void testDefaultSuperTypeConfig() {
            CodecClassProfile profile = getPersonProfile();

            SuperTypeSerializationConfig superConfig = profile.getSuperTypeConfig();
            assertNotNull(superConfig, "SuperTypeConfig should always be present (with defaults)");
            assertFalse(superConfig.isEnabled());
            assertEquals(SuperTypeSelection.ALL, superConfig.getSelection());
            assertEquals(SerializationFormat.PLAIN, superConfig.getFormat());
            assertTrue(superConfig.isAsArray());
            assertEquals(",", superConfig.getSeparator());
            assertEquals("_supertype", superConfig.getSuperTypeKey());
        }

        @Test
        @DisplayName("default FeatureConfig has correct defaults")
        void testDefaultFeatureConfig() {
            CodecClassProfile profile = getPersonProfile();

            assertFalse(profile.getFeatureConfigs().isEmpty(), "Should have feature configs");

            // Find the "name" feature config
            FeatureSerializationConfig nameConfig = profile.getFeatureConfigs().stream()
                    .filter(fc -> "name".equals(fc.getFeatureName()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(nameConfig, "Should have feature config for 'name'");

            assertEquals("name", nameConfig.getKey(), "Key should default to feature name");
            assertEquals(Boolean.FALSE, nameConfig.getIgnore(), "Should not be ignored by default");
            assertNull(nameConfig.getValueWriterName());
            assertNull(nameConfig.getValueReaderName());
            assertEquals(EnumSerializationStrategy.LITERAL, nameConfig.getEnumSerialization(),
                    "EMF enum default is LITERAL");
        }

        @Test
        @DisplayName("reference feature config has no reference-specific configs by default")
        void testDefaultReferenceFeatureConfig() {
            CodecClassProfile profile = getPersonProfile();

            FeatureSerializationConfig addressConfig = profile.getFeatureConfigs().stream()
                    .filter(fc -> "address".equals(fc.getFeatureName()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(addressConfig, "Should have feature config for 'address'");

            assertEquals("address", addressConfig.getKey());
            assertEquals(Boolean.FALSE, addressConfig.getIgnore(), "Should not be ignored by default");
            assertNull(addressConfig.getReferenceConfig(), "No reference config without annotation");
            assertNull(addressConfig.getTypeConfig(), "No type config without annotation");
        }
    }

    // ========================================================================
    // Pre-Merged Profile State Tests — Annotated Class Profiles
    // ========================================================================

    @Nested
    @DisplayName("Annotated Class Profile")
    class AnnotatedClassProfileTests {

        private EPackage annotatedPackage;
        private EClass annotatedClass;

        @BeforeEach
        void setUpAnnotated() {
            annotatedPackage = EcoreFactory.eINSTANCE.createEPackage();
            annotatedPackage.setName("annotated");
            annotatedPackage.setNsURI("http://test.example.org/annotated/1.0");
            annotatedPackage.setNsPrefix("annotated");

            annotatedClass = EcoreFactory.eINSTANCE.createEClass();
            annotatedClass.setName("AnnotatedPerson");
            annotatedPackage.getEClassifiers().add(annotatedClass);

            EAttribute nameAttr = EcoreFactory.eINSTANCE.createEAttribute();
            nameAttr.setName("name");
            nameAttr.setEType(EcorePackage.Literals.ESTRING);
            annotatedClass.getEStructuralFeatures().add(nameAttr);
        }

        private void addClassAnnotation(String... keyValuePairs) {
            EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
            ann.setSource("http://eclipse.org/fennec/codec");
            for (int i = 0; i < keyValuePairs.length; i += 2) {
                ann.getDetails().put(keyValuePairs[i], keyValuePairs[i + 1]);
            }
            annotatedClass.getEAnnotations().add(ann);
        }

        private CodecClassProfile getProfile() {
            CodecAspectProvider codecProvider = new CodecAspectProvider();
            service.registerAspectProvider(codecProvider);
            service.registerPackage(annotatedPackage);

            ClassProfile classProfile = service.getClassProfile(annotatedClass, "codec");
            assertNotNull(classProfile);
            return (CodecClassProfile) classProfile;
        }

        @Test
        @DisplayName("annotated TypeConfig copies strategy and key")
        void testAnnotatedTypeConfig() {
            addClassAnnotation("typeStrategy", "NAME", "typeKey", "objectType");

            CodecClassProfile profile = getProfile();
            TypeSerializationConfig typeConfig = profile.getTypeConfig();

            assertNotNull(typeConfig);
            assertEquals(TypeStrategy.NAME, typeConfig.getStrategy());
            assertEquals("objectType", typeConfig.getTypeKey());
            // Non-annotated fields keep EMF defaults
            assertEquals(SerializationFormat.PLAIN, typeConfig.getFormat());
        }

        @Test
        @DisplayName("annotated IdConfig copies strategy and idKey")
        void testAnnotatedIdConfig() {
            addClassAnnotation("idStrategy", "COMBINED", "idKey", "identifier",
                    "idFeatures", "name", "idSeparator", ":");

            CodecClassProfile profile = getProfile();
            IdSerializationConfig idConfig = profile.getIdConfig();

            assertNotNull(idConfig);
            assertEquals(IdStrategy.COMBINED, idConfig.getStrategy());
            assertEquals("identifier", idConfig.getIdKey());
            assertEquals(":", idConfig.getSeparator());
            assertTrue(idConfig.getIdFeatures().contains("name"));
        }

        @Test
        @DisplayName("annotated SuperTypeConfig copies enabled and selection")
        void testAnnotatedSuperTypeConfig() {
            addClassAnnotation("superTypeSerialize", "true", "superTypeStrategy", "SINGLE",
                    "superTypeKey", "parentTypes");

            CodecClassProfile profile = getProfile();
            SuperTypeSerializationConfig superConfig = profile.getSuperTypeConfig();

            assertNotNull(superConfig);
            assertTrue(superConfig.isEnabled());
            assertEquals(SuperTypeSelection.SINGLE, superConfig.getSelection());
            assertEquals("parentTypes", superConfig.getSuperTypeKey());
        }

        @Test
        @DisplayName("annotated TypeConfig is a copy, not the aspect's instance")
        void testTypeConfigIsCopy() {
            addClassAnnotation("typeStrategy", "NAME");

            CodecAspectProvider codecProvider = new CodecAspectProvider();
            service.registerAspectProvider(codecProvider);
            service.registerPackage(annotatedPackage);

            CodecClassProfile profile = (CodecClassProfile) service.getClassProfile(annotatedClass, "codec");
            assertNotNull(profile.getTypeConfig());

            // The profile's config should be a copy, not shared with aspect
            // (Verifying EcoreUtil.copy was used)
            PackageMetadata pkgMeta = service.getPackageMetadata("http://test.example.org/annotated/1.0");
            ClassMetadata classMeta = pkgMeta.getClasses().stream()
                    .filter(cm -> cm.getEClass() == annotatedClass)
                    .findFirst()
                    .orElse(null);
            assertNotNull(classMeta);
        }

        @Test
        @DisplayName("structured IdConfig preserves all fields")
        void testStructuredIdConfig() {
            addClassAnnotation("idStrategy", "COMBINED", "idFormat", "STRUCTURED",
                    "idKeyMode", "BOTH", "idOnTop", "false",
                    "idFeatures", "name", "idValueKey", "identifier");

            CodecClassProfile profile = getProfile();
            IdSerializationConfig idConfig = profile.getIdConfig();

            assertEquals(IdStrategy.COMBINED, idConfig.getStrategy());
            assertEquals(SerializationFormat.STRUCTURED, idConfig.getFormat());
            assertEquals(IdKeyMode.BOTH, idConfig.getKeyMode());
            assertFalse(idConfig.isOnTop());
            assertEquals("identifier", idConfig.getValueKey());
        }
    }

    // ========================================================================
    // Pre-Merged Profile State Tests — Annotated Feature Profiles
    // ========================================================================

    @Nested
    @DisplayName("Annotated Feature Profile")
    class AnnotatedFeatureProfileTests {

        private EPackage featurePackage;
        private EClass entityClass;
        private EClass targetClass;
        private EAttribute customKeyAttr;
        private EAttribute transientAttr;
        private EReference typedRef;

        @BeforeEach
        void setUpFeatures() {
            featurePackage = EcoreFactory.eINSTANCE.createEPackage();
            featurePackage.setName("features");
            featurePackage.setNsURI("http://test.example.org/features/1.0");
            featurePackage.setNsPrefix("features");

            targetClass = EcoreFactory.eINSTANCE.createEClass();
            targetClass.setName("Target");
            featurePackage.getEClassifiers().add(targetClass);

            entityClass = EcoreFactory.eINSTANCE.createEClass();
            entityClass.setName("Entity");
            featurePackage.getEClassifiers().add(entityClass);

            // Attribute with custom key
            customKeyAttr = EcoreFactory.eINSTANCE.createEAttribute();
            customKeyAttr.setName("firstName");
            customKeyAttr.setEType(EcorePackage.Literals.ESTRING);
            entityClass.getEStructuralFeatures().add(customKeyAttr);
            addFeatureAnnotation(customKeyAttr, "key", "first_name");

            // Transient attribute
            transientAttr = EcoreFactory.eINSTANCE.createEAttribute();
            transientAttr.setName("secret");
            transientAttr.setEType(EcorePackage.Literals.ESTRING);
            entityClass.getEStructuralFeatures().add(transientAttr);
            addFeatureAnnotation(transientAttr, "ignore", "true");

            // Reference with type config
            typedRef = EcoreFactory.eINSTANCE.createEReference();
            typedRef.setName("target");
            typedRef.setEType(targetClass);
            typedRef.setContainment(false);
            entityClass.getEStructuralFeatures().add(typedRef);
            addFeatureAnnotation(typedRef,
                    "typeStrategy", "NAME", "typeKey", "_refType",
                    "refFormat", "STRUCTURED", "refKey", "$ref",
                    "expand", "true");
        }

        private void addFeatureAnnotation(EStructuralFeature feature, String... keyValuePairs) {
            EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
            ann.setSource("http://eclipse.org/fennec/codec");
            for (int i = 0; i < keyValuePairs.length; i += 2) {
                ann.getDetails().put(keyValuePairs[i], keyValuePairs[i + 1]);
            }
            feature.getEAnnotations().add(ann);
        }

        private CodecClassProfile getEntityProfile() {
            CodecAspectProvider codecProvider = new CodecAspectProvider();
            service.registerAspectProvider(codecProvider);
            service.registerPackage(featurePackage);

            ClassProfile classProfile = service.getClassProfile(entityClass, "codec");
            assertNotNull(classProfile);
            return (CodecClassProfile) classProfile;
        }

        @Test
        @DisplayName("feature with custom key uses key from annotation")
        void testFeatureWithCustomKey() {
            CodecClassProfile profile = getEntityProfile();

            FeatureSerializationConfig config = profile.getFeatureConfigs().stream()
                    .filter(fc -> "firstName".equals(fc.getFeatureName()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(config);

            assertEquals("first_name", config.getKey());
            assertEquals(Boolean.FALSE, config.getIgnore(), "Should not be ignored");
        }

        @Test
        @DisplayName("transient feature has ignore=true")
        void testTransientFeature() {
            CodecClassProfile profile = getEntityProfile();

            FeatureSerializationConfig config = profile.getFeatureConfigs().stream()
                    .filter(fc -> "secret".equals(fc.getFeatureName()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(config);

            assertEquals(Boolean.TRUE, config.getIgnore());
        }

        @Test
        @DisplayName("reference feature has type config and reference config")
        void testReferenceFeatureConfigs() {
            CodecClassProfile profile = getEntityProfile();

            FeatureSerializationConfig config = profile.getFeatureConfigs().stream()
                    .filter(fc -> "target".equals(fc.getFeatureName()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(config);

            // Type config should be copied from reference aspect
            assertNotNull(config.getTypeConfig(), "Reference should have type config");
            assertEquals(TypeStrategy.NAME, config.getTypeConfig().getStrategy());
            assertEquals("_refType", config.getTypeConfig().getTypeKey());

            // Reference config should be copied from reference aspect
            assertNotNull(config.getReferenceConfig(), "Reference should have reference config");
            assertEquals(SerializationFormat.STRUCTURED, config.getReferenceConfig().getFormat());
            assertEquals("$ref", config.getReferenceConfig().getRefKey());

            // Expand should be set
            assertEquals(Boolean.TRUE, config.getExpand());
        }
    }

    // ========================================================================
    // Pre-Merged Profile State Tests — Profile Structure
    // ========================================================================

    @Nested
    @DisplayName("Profile Structure")
    class ProfileStructureTests {

        @Test
        @DisplayName("profile has one feature config per feature")
        void testFeatureConfigCountMatchesFeatures() {
            CodecAspectProvider codecProvider = new CodecAspectProvider();
            service.registerAspectProvider(codecProvider);
            service.registerPackage(testPackage);

            CodecClassProfile profile = (CodecClassProfile) service.getClassProfile(personClass, "codec");
            assertNotNull(profile);

            // Person has "name" (EAttribute) and "address" (EReference) = 2 features
            assertEquals(2, profile.getFeatureConfigs().size(),
                    "Should have one feature config per structural feature");
        }

        @Test
        @DisplayName("feature configs are in same order as EClass features")
        void testFeatureConfigOrder() {
            CodecAspectProvider codecProvider = new CodecAspectProvider();
            service.registerAspectProvider(codecProvider);
            service.registerPackage(testPackage);

            CodecClassProfile profile = (CodecClassProfile) service.getClassProfile(personClass, "codec");
            assertNotNull(profile);

            // personClass features: name, address (in that order)
            assertEquals("name", profile.getFeatureConfigs().get(0).getFeatureName());
            assertEquals("address", profile.getFeatureConfigs().get(1).getFeatureName());
        }

        @Test
        @DisplayName("class without features has empty feature configs")
        void testClassWithoutFeatures() {
            CodecAspectProvider codecProvider = new CodecAspectProvider();
            service.registerAspectProvider(codecProvider);
            service.registerPackage(testPackage);

            // addressClass has no features
            CodecClassProfile profile = (CodecClassProfile) service.getClassProfile(addressClass, "codec");
            assertNotNull(profile);

            assertTrue(profile.getFeatureConfigs().isEmpty(),
                    "Class without features should have no feature configs");
            // But class configs should still be present with defaults
            assertNotNull(profile.getTypeConfig());
            assertNotNull(profile.getIdConfig());
            assertNotNull(profile.getSuperTypeConfig());
        }

        @Test
        @DisplayName("package profile has correct number of class profiles")
        void testPackageProfileClassCount() {
            CodecAspectProvider codecProvider = new CodecAspectProvider();
            service.registerAspectProvider(codecProvider);
            service.registerPackage(testPackage);

            PackageProfile pkgProfile = service.getPackageProfile(testPackage, "codec");
            assertNotNull(pkgProfile);
            assertTrue(pkgProfile instanceof CodecPackageProfile);

            // testPackage has Person and Address = 2 classes
            assertEquals(2, pkgProfile.getClassProfiles().size());
        }

        @Test
        @DisplayName("class profiles reference correct EClasses")
        void testClassProfileEClasses() {
            CodecAspectProvider codecProvider = new CodecAspectProvider();
            service.registerAspectProvider(codecProvider);
            service.registerPackage(testPackage);

            CodecPackageProfile pkgProfile = (CodecPackageProfile)
                    service.getPackageProfile(testPackage, "codec");
            assertNotNull(pkgProfile);

            boolean foundPerson = false;
            boolean foundAddress = false;
            for (ClassProfile cp : pkgProfile.getClassProfiles()) {
                if (cp.getEClass() == personClass) foundPerson = true;
                if (cp.getEClass() == addressClass) foundAddress = true;
            }
            assertTrue(foundPerson, "Should have profile for Person");
            assertTrue(foundAddress, "Should have profile for Address");
        }
    }

    // ========================================================================
    // Test Provider that builds profiles using concrete codec EMF types
    // ========================================================================

    /**
     * AspectProvider that uses CodecAspectProvider for aspects but captures
     * the buildProfiles parameter and returns concrete CodecPackageProfile/CodecClassProfile.
     */
    private static class ProfileCapturingProvider extends CodecAspectProvider {

        PackageMetadata receivedMetadataCopy;

        @Override
        public String getAspectTypeId() {
            return "test-profile";
        }

        @Override
        public PackageProfile buildProfiles(PackageMetadata filteredMetadataCopy) {
            this.receivedMetadataCopy = filteredMetadataCopy;

            CodecPackageProfile pkgProfile = CodecFactory.eINSTANCE.createCodecPackageProfile();
            for (ClassMetadata classMeta : filteredMetadataCopy.getClasses()) {
                CodecClassProfile classProfile = CodecFactory.eINSTANCE.createCodecClassProfile();
                classProfile.setEClass(classMeta.getEClass());
                pkgProfile.getClassProfiles().add(classProfile);
            }
            return pkgProfile;
        }
    }
}
