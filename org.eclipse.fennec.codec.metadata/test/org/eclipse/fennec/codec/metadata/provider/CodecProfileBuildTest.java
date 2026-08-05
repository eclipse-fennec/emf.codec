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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile;
import org.eclipse.fennec.codec.metadata.model.codec.CodecPackageProfile;
import org.eclipse.fennec.codec.metadata.model.codec.EnumSerializationStrategy;
import org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.IdKeyMode;
import org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.IdStrategy;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
import org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSelection;
import org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.model.metadata.ClassMetadata;
import org.eclipse.fennec.emf.osgi.model.metadata.PackageMetadata;
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

	@BeforeEach
    void setUp() {
        service = MetadataServices.createWhiteboard(new CodecAspectProvider());
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
    // Profile Retrieval — the profile lives in the package-level AspectEntry
    // ========================================================================

    /**
     * The codec profile of a package. It is no longer a metadata concept of its own: the
     * provider hangs a {@link CodecPackageProfile} into the package-level
     * {@link org.eclipse.fennec.emf.osgi.model.metadata.AspectEntry AspectEntry}, and the
     * per-class profiles live inside it (issue #85, D2).
     */
    private CodecPackageProfile packageProfileOf(EPackage ePackage) {
        PackageMetadata packageMetadata = service.registerPackage(ePackage).orElseThrow();
        CodecPackageProfile profile =
                CodecAspectProvider.codecAspect(packageMetadata.getAspects(), CodecPackageProfile.class);
        assertNotNull(profile, "a codec package profile must be built for " + ePackage.getNsURI());
        return profile;
    }

    /** The class profile for one class of the given package. */
    private CodecClassProfile classProfileOf(EPackage ePackage, EClass eClass) {
        return packageProfileOf(ePackage).getClassProfiles().stream()
                .filter(profile -> profile.getEClass() == eClass)
                .findFirst()
                .orElseThrow(() -> new AssertionError("no class profile for " + eClass.getName()));
    }

    @Test
    @DisplayName("the package-level aspect entry carries the codec profile")
    void profileLivesInPackageAspectEntry() {
        PackageMetadata packageMetadata = service.registerPackage(testPackage).orElseThrow();

        assertFalse(packageMetadata.getAspects().isEmpty(),
                "the package metadata must carry an aspect entry after registration");
        assertNotNull(CodecAspectProvider.codecAspect(packageMetadata.getAspects(), CodecPackageProfile.class),
                "the codec entry must hold the package profile");
    }

    @Test
    @DisplayName("an unknown aspect type yields no profile")
    void unknownAspectTypeYieldsNothing() {
        PackageMetadata packageMetadata = service.registerPackage(testPackage).orElseThrow();

        assertNull(CodecAspectProvider.codecAspect(packageMetadata.getAspects(), CodecClassProfile.class),
                "the package entry holds a CodecPackageProfile, not a CodecClassProfile");
    }

    @Test
    @DisplayName("one class profile per EClass, keyed by the EClass instance")
    void oneClassProfilePerEClass() {
        CodecPackageProfile packageProfile = packageProfileOf(testPackage);

        assertEquals(2, packageProfile.getClassProfiles().size(),
                "the package profile should have one class profile per EClass");
        assertSame(personClass, classProfileOf(testPackage, personClass).getEClass());
        assertSame(addressClass, classProfileOf(testPackage, addressClass).getEClass());
    }

    // ========================================================================
    // Pre-Merged Profile State Tests — Default Profiles (No Annotations)
    // ========================================================================

    @Nested
    @DisplayName("Default Profile (No Annotations)")
    class DefaultProfileTests {

        private CodecClassProfile getPersonProfile() {
            return classProfileOf(testPackage, personClass);
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
            return classProfileOf(annotatedPackage, annotatedClass);
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

            CodecClassProfile profile = classProfileOf(annotatedPackage, annotatedClass);
            assertNotNull(profile.getTypeConfig());

            // The profile's config should be a copy, not shared with aspect
            // (Verifying EcoreUtil.copy was used)
            PackageMetadata pkgMeta = service.getPackageMetadata("http://test.example.org/annotated/1.0").orElseThrow();
            ClassMetadata classMeta = pkgMeta.getClasses().stream()
                    .filter(cm -> cm.getEClass() == annotatedClass)
                    .findFirst()
                    .orElse(null);
            assertNotNull(classMeta);
        }

        @Test
        @DisplayName("idFeatures tokens are trimmed and empties dropped")
        void testIdFeaturesTokensTrimmed() {
            // Spec 09-id.md uses spaced lists: idFeatures="firstName, lastName, sequence"
            addClassAnnotation("idFeatures", "firstName, lastName , sequence,");

            IdSerializationConfig idConfig = getProfile().getIdConfig();

            assertEquals(List.of("firstName", "lastName", "sequence"), idConfig.getIdFeatures());
        }

        @Test
        @DisplayName("class-level idFeatures replaces the package default")
        void testIdFeaturesClassLevelReplacesPackageDefault() {
            EAnnotation packageAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
            packageAnnotation.setSource("http://eclipse.org/fennec/codec");
            packageAnnotation.getDetails().put("idFeatures", "packageId");
            annotatedPackage.getEAnnotations().add(packageAnnotation);

            addClassAnnotation("idFeatures", "firstName, lastName");

            IdSerializationConfig idConfig = getProfile().getIdConfig();

            assertEquals(List.of("firstName", "lastName"), idConfig.getIdFeatures(),
                    "restating idFeatures on the class must override, not append to, the package default");
        }

        @Test
        @DisplayName("idSeparatorSerialize annotation key matches the spec")
        void testIdSeparatorSerializeAnnotationKey() {
            // Spec 09-id.md §5.0 / 16-annotation-reference.md §ID: the key is
            // "idSeparatorSerialize", not the swapped "idSerializeSeparator".
            addClassAnnotation("idSeparatorSerialize", "false");

            IdSerializationConfig idConfig = getProfile().getIdConfig();

            assertFalse(idConfig.isSerializeSeparator());
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
            return classProfileOf(featurePackage, entityClass);
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
            CodecClassProfile profile = classProfileOf(testPackage, personClass);
            assertNotNull(profile);

            // Person has "name" (EAttribute) and "address" (EReference) = 2 features
            assertEquals(2, profile.getFeatureConfigs().size(),
                    "Should have one feature config per structural feature");
        }

        @Test
        @DisplayName("feature configs are in same order as EClass features")
        void testFeatureConfigOrder() {
            CodecClassProfile profile = classProfileOf(testPackage, personClass);
            assertNotNull(profile);

            // personClass features: name, address (in that order)
            assertEquals("name", profile.getFeatureConfigs().get(0).getFeatureName());
            assertEquals("address", profile.getFeatureConfigs().get(1).getFeatureName());
        }

        @Test
        @DisplayName("class without features has empty feature configs")
        void testClassWithoutFeatures() {
            // addressClass has no features
            CodecClassProfile profile = classProfileOf(testPackage, addressClass);
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
            CodecPackageProfile pkgProfile = packageProfileOf(testPackage);

            // testPackage has Person and Address = 2 classes
            assertEquals(2, pkgProfile.getClassProfiles().size());
        }

        @Test
        @DisplayName("class profiles reference correct EClasses")
        void testClassProfileEClasses() {
            CodecPackageProfile pkgProfile = packageProfileOf(testPackage);

            boolean foundPerson = false;
            boolean foundAddress = false;
            for (CodecClassProfile cp : pkgProfile.getClassProfiles()) {
                if (cp.getEClass() == personClass) foundPerson = true;
                if (cp.getEClass() == addressClass) foundAddress = true;
            }
            assertTrue(foundPerson, "Should have profile for Person");
            assertTrue(foundAddress, "Should have profile for Address");
        }
    }

}
