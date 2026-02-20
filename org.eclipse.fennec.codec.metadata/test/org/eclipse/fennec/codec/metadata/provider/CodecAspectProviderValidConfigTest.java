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

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect;
import org.eclipse.fennec.model.metadata.AttributeMetadata;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.FeatureMetadata;
import org.eclipse.fennec.model.metadata.MetadataFactory;
import org.eclipse.fennec.model.metadata.ReferenceMetadata;
import org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig;
import org.eclipse.fennec.model.metadata.ClassAspect;
import org.eclipse.fennec.model.metadata.EnumSerializationStrategy;
import org.eclipse.fennec.model.metadata.FeatureAspect;
import org.eclipse.fennec.model.metadata.IdKeyMode;
import org.eclipse.fennec.model.metadata.IdStrategy;
import org.eclipse.fennec.model.metadata.SerializationFormat;
import org.eclipse.fennec.model.metadata.SuperTypeSelection;
import org.eclipse.fennec.model.metadata.TypeStrategy;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for valid codec annotation configurations.
 * <p>
 * These tests verify that correctly placed annotation keys are parsed
 * into the appropriate Aspect EMF objects.
 * </p>
 * <p>
 * All test models use @VALID tags in test-codec-annotations.ecore.
 * </p>
 *
 * @see CodecAspectProvider
 * @see CodecAspectProviderMisconfigTest for misconfiguration tests
 */
@DisplayName("CodecAspectProvider - Valid Configurations")
class CodecAspectProviderValidConfigTest {

    private static final String TEST_ECORE = "test-codec-annotations.ecore";

    private CodecAspectProvider provider;
    private EcoreHelper helper;
    private EPackage testPackage;

    @BeforeEach
    void setUp() throws IOException {
        provider = new CodecAspectProvider();
        helper = new EcoreHelper();
        testPackage = helper.loadEcore(TEST_ECORE, CodecAspectProviderValidConfigTest.class);
    }

    @AfterEach
    void tearDown() {
        helper.releaseAll();
    }

    // ========================================================================
    // Wrapper Helpers for Metadata Types
    // ========================================================================

    private ClassMetadata wrapClass(EClass eClass) {
        ClassMetadata md = MetadataFactory.eINSTANCE.createClassMetadata();
        md.setEClass(eClass);
        md.setName(eClass.getName());
        return md;
    }

    private AttributeMetadata wrapAttribute(EAttribute attr) {
        AttributeMetadata md = MetadataFactory.eINSTANCE.createAttributeMetadata();
        md.setEFeature(attr);
        md.setEAttribute(attr);
        md.setName(attr.getName());
        return md;
    }

    private ReferenceMetadata wrapReference(EReference ref) {
        ReferenceMetadata md = MetadataFactory.eINSTANCE.createReferenceMetadata();
        md.setEFeature(ref);
        md.setEReference(ref);
        md.setName(ref.getName());
        return md;
    }

    private FeatureMetadata wrapFeature(EStructuralFeature feature) {
        if (feature instanceof EAttribute attr) return wrapAttribute(attr);
        if (feature instanceof EReference ref) return wrapReference(ref);
        throw new IllegalArgumentException("Unknown feature type");
    }

    // ========================================================================
    // Basic Tests
    // ========================================================================

    @Nested
    @DisplayName("Basic Aspect Creation")
    class BasicTests {

        /** @HELPER Tests provider type ID. */
        @Test
        @DisplayName("provider returns 'codec' type ID")
        void helper_aspectTypeId_returnsCodec() {
            assertEquals("codec", provider.getAspectTypeId());
        }

        /** @VALID @SPEC(16-annotation-reference.md) Tests class without annotations uses defaults. */
        @Test
        @DisplayName("class without annotations uses defaults")
        void validConfig_classNoAnnotations_usesDefaults() {
            EClass simpleClass = EcoreHelper.getEClass(testPackage, "SimpleClass");

            ClassAspect aspect = provider.buildClassAspect(wrapClass(simpleClass));

            assertNotNull(aspect);
            assertTrue(aspect instanceof ClassCodecAspect);
            assertEquals("codec", aspect.getTypeId());

            ClassCodecAspect codecAspect = (ClassCodecAspect) aspect;
            assertNull(codecAspect.getIdConfig());
            assertNull(codecAspect.getTypeConfig());
            assertNull(codecAspect.getSuperTypeConfig());
            assertTrue(codecAspect.isInheritFromParent());
        }

        /** @VALID Tests attribute without annotations uses defaults. */
        @Test
        @DisplayName("attribute without annotations uses defaults")
        void validConfig_attributeNoAnnotations_usesDefaults() {
            EClass simpleClass = EcoreHelper.getEClass(testPackage, "SimpleClass");
            EAttribute nameAttr = (EAttribute) EcoreHelper.getFeature(simpleClass, "name");

            FeatureAspect aspect = provider.buildAttributeAspect(wrapAttribute(nameAttr));

            assertNotNull(aspect);
            assertTrue(aspect instanceof FeatureCodecAspect);
            assertEquals("codec", aspect.getTypeId());

            FeatureCodecAspect codecAspect = (FeatureCodecAspect) aspect;
            assertFalse(codecAspect.isIgnore());
            assertNull(codecAspect.getEffectiveKey());
            assertNull(codecAspect.getValueWriterName());
            assertNull(codecAspect.getValueReaderName());
        }

        /** @VALID Tests reference without annotations uses defaults. */
        @Test
        @DisplayName("reference without annotations uses defaults")
        void validConfig_referenceNoAnnotations_usesDefaults() {
            EClass personClass = EcoreHelper.getEClass(testPackage, "PersonWithTypedReference");
            EReference addressRef = (EReference) EcoreHelper.getFeature(personClass, "address");

            // Remove annotation for this test
            addressRef.getEAnnotations().clear();

            FeatureAspect aspect = provider.buildReferenceAspect(wrapReference(addressRef));

            assertNotNull(aspect);
            assertTrue(aspect instanceof ReferenceCodecAspect);
            assertEquals("codec", aspect.getTypeId());

            ReferenceCodecAspect codecAspect = (ReferenceCodecAspect) aspect;
            assertFalse(codecAspect.isIgnore());
            assertNull(codecAspect.getEffectiveKey());
        }

        /** @HELPER Tests delegation from buildFeatureAspect. */
        @Test
        @DisplayName("buildFeatureAspect delegates to correct method")
        void helper_featureAspectDelegation_delegatesCorrectly() {
            EClass simpleClass = EcoreHelper.getEClass(testPackage, "SimpleClass");
            EAttribute nameAttr = (EAttribute) EcoreHelper.getFeature(simpleClass, "name");

            EClass personClass = EcoreHelper.getEClass(testPackage, "PersonWithTypedReference");
            EReference addressRef = (EReference) EcoreHelper.getFeature(personClass, "address");

            FeatureAspect attrAspect = provider.buildFeatureAspect(wrapFeature(nameAttr));
            assertTrue(attrAspect instanceof FeatureCodecAspect);
            assertFalse(attrAspect instanceof ReferenceCodecAspect);

            FeatureAspect refAspect = provider.buildFeatureAspect(wrapFeature(addressRef));
            assertTrue(refAspect instanceof ReferenceCodecAspect);
        }
    }

    // ========================================================================
    // ID Configuration Tests
    // ========================================================================

    @Nested
    @DisplayName("ID Configuration")
    class IdConfigTests {

        /** @VALID @SPEC(09-id.md) Tests ID_FIELD strategy parsing. */
        @Test
        @DisplayName("ID_FIELD strategy parsed correctly")
        void validConfig_idFieldStrategy_parsedCorrectly() {
            EClass personClass = EcoreHelper.getEClass(testPackage, "PersonWithIdField");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(personClass));

            assertNotNull(aspect.getIdConfig());
            IdSerializationConfig idConfig = aspect.getIdConfig();
            assertEquals(IdStrategy.ID_FIELD, idConfig.getStrategy());
            assertEquals("_id", idConfig.getIdKey());
        }

        /** @VALID @SPEC(09-id.md) Tests COMBINED strategy with features and separator. */
        @Test
        @DisplayName("COMBINED strategy with features and separator")
        void validConfig_combinedIdStrategy_parsedWithFeaturesAndSeparator() {
            EClass personClass = EcoreHelper.getEClass(testPackage, "PersonWithCombinedId");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(personClass));

            assertNotNull(aspect.getIdConfig());
            IdSerializationConfig idConfig = aspect.getIdConfig();
            assertEquals(IdStrategy.COMBINED, idConfig.getStrategy());
            assertEquals("-", idConfig.getSeparator());
            assertEquals(2, idConfig.getIdFeatures().size());
            assertTrue(idConfig.getIdFeatures().contains("firstName"));
            assertTrue(idConfig.getIdFeatures().contains("lastName"));
        }

        /** @VALID @SPEC(09-id.md) Tests NONE keyMode (disables ID serialization). Per spec, NONE was moved from IdStrategy to IdKeyMode. */
        @Test
        @DisplayName("NONE keyMode parsed correctly")
        void validConfig_idNoneKeyMode_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithNoId");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getIdConfig());
            assertEquals(IdKeyMode.NONE, aspect.getIdConfig().getKeyMode());
        }

        /** @VALID @SPEC(09-id.md) Tests custom ID reader/writer names. */
        @Test
        @DisplayName("custom ID reader/writer names")
        void validConfig_customIdReaderWriter_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithCustomIdHandlers");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getIdConfig());
            assertEquals("CustomIdReader", aspect.getIdConfig().getIdValueReaderName());
            assertEquals("CustomIdWriter", aspect.getIdConfig().getIdValueWriterName());
        }

        /** @VALID @SPEC(09-id.md) Tests structured ID config with format/keyMode/onTop. */
        @Test
        @DisplayName("structured ID config with format/keyMode/onTop")
        void validConfig_structuredIdConfig_allFieldsParsed() {
            EClass personClass = EcoreHelper.getEClass(testPackage, "PersonWithStructuredId");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(personClass));

            assertNotNull(aspect.getIdConfig());
            IdSerializationConfig idConfig = aspect.getIdConfig();

            assertEquals(IdStrategy.COMBINED, idConfig.getStrategy());
            assertEquals(SerializationFormat.STRUCTURED, idConfig.getFormat());
            assertEquals(IdKeyMode.BOTH, idConfig.getKeyMode());
            assertFalse(idConfig.isOnTop());
            assertTrue(idConfig.isSerializeSeparator());
            assertEquals("sep", idConfig.getSeparatorKey());
            assertEquals(2, idConfig.getIdFeatures().size());
        }

        /** @VALID @SPEC(09-id.md§2) Tests idKeyMode=NONE parsing - no ID serialization. */
        @Test
        @DisplayName("idKeyMode=NONE disables ID serialization")
        void validConfig_idKeyModeNone_parsedCorrectly() {
            EClass personClass = EcoreHelper.getEClass(testPackage, "PersonWithIdKeyModeNone");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(personClass));

            assertNotNull(aspect.getIdConfig());
            IdSerializationConfig idConfig = aspect.getIdConfig();

            assertEquals(IdKeyMode.NONE, idConfig.getKeyMode());
            // Strategy is still parsed (COMBINED) but will be ignored at runtime when keyMode=NONE
            assertEquals(IdStrategy.COMBINED, idConfig.getStrategy());
            assertEquals(2, idConfig.getIdFeatures().size());
        }

        /** @VALID @SPEC(09-id.md) Tests idValueKey for structured format. */
        @Test
        @DisplayName("idValueKey for structured format")
        void validConfig_idValueKey_parsedCorrectly() {
            EClass personClass = EcoreHelper.getEClass(testPackage, "PersonWithIdValueKey");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(personClass));

            assertNotNull(aspect.getIdConfig());
            IdSerializationConfig idConfig = aspect.getIdConfig();

            assertEquals(IdStrategy.COMBINED, idConfig.getStrategy());
            assertEquals(SerializationFormat.STRUCTURED, idConfig.getFormat());
            assertEquals("identifier", idConfig.getValueKey());
            assertEquals(2, idConfig.getIdFeatures().size());
        }
    }

    // ========================================================================
    // Type Configuration Tests
    // ========================================================================

    @Nested
    @DisplayName("Type Configuration")
    class TypeConfigTests {

        /** @VALID @SPEC(06-type.md) Tests URI type strategy. */
        @Test
        @DisplayName("URI type strategy parsed correctly")
        void validConfig_typeStrategyUri_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "TypedEntityUri");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getTypeConfig());
            TypeSerializationConfig typeConfig = aspect.getTypeConfig();
            assertEquals(TypeStrategy.URI, typeConfig.getStrategy());
            assertEquals("_type", typeConfig.getTypeKey());
        }

        /** @VALID @SPEC(06-type.md) Tests NAME type strategy. */
        @Test
        @DisplayName("NAME type strategy parsed correctly")
        void validConfig_typeStrategyName_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "TypedEntityName");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getTypeConfig());
            assertEquals(TypeStrategy.NAME, aspect.getTypeConfig().getStrategy());
        }

        /** @VALID @SPEC(08-discriminator-mapping.md) Tests discriminator path. */
        @Test
        @DisplayName("discriminator path parsed correctly")
        void validConfig_discriminatorPath_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "TypedEntityWithDiscriminator");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getTypeConfig());
            TypeSerializationConfig typeConfig = aspect.getTypeConfig();
            assertEquals(TypeStrategy.NAME, typeConfig.getStrategy());
            assertEquals("deviceInfo.profileName", typeConfig.getDiscriminatorPath());
        }

        /** @VALID @SPEC(06-type.md) Tests TypeStrategy.NONE for suppressing type info. */
        @Test
        @DisplayName("TypeStrategy.NONE suppresses type information")
        void validConfig_typeStrategyNone_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "TypedEntityNoInclude");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getTypeConfig());
            assertEquals(TypeStrategy.NONE, aspect.getTypeConfig().getStrategy());
        }

        /** @VALID @SPEC(06-type.md) Tests structured type config with format/schemaKey/nameKey. */
        @Test
        @DisplayName("structured type config with format/schemaKey/nameKey")
        void validConfig_structuredTypeConfig_allFieldsParsed() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "TypedEntityStructured");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getTypeConfig());
            TypeSerializationConfig typeConfig = aspect.getTypeConfig();

            assertEquals(TypeStrategy.SCHEMA_AND_TYPE, typeConfig.getStrategy());
            assertEquals(SerializationFormat.STRUCTURED, typeConfig.getFormat());
            assertEquals("$schema", typeConfig.getSchemaKey());
            assertEquals("typeName", typeConfig.getNameKey());
            assertEquals("typeInfo", typeConfig.getTypeKey());
        }

        /** @VALID @SPEC(08-discriminator-mapping.md) Tests typeMapId for registry lookup. */
        @Test
        @DisplayName("typeMapId for registry lookup")
        void validConfig_typeMapId_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "TypedEntityWithMapId");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getTypeConfig());
            TypeSerializationConfig typeConfig = aspect.getTypeConfig();

            assertEquals(TypeStrategy.NAME, typeConfig.getStrategy());
            assertEquals("my-custom-registry", typeConfig.getMapId());
        }
    }

    // ========================================================================
    // SuperType Configuration Tests
    // ========================================================================

    @Nested
    @DisplayName("SuperType Configuration")
    class SuperTypeConfigTests {

        /** @VALID @SPEC(07-supertype.md) Tests supertype serialization enabled. */
        @Test
        @DisplayName("supertype serialization enabled")
        void validConfig_superTypeEnabled_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithSuperTypes");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getSuperTypeConfig());
            SuperTypeSerializationConfig superConfig = aspect.getSuperTypeConfig();
            assertTrue(superConfig.isEnabled());
            assertEquals("_superTypes", superConfig.getSuperTypeKey());
            assertEquals(SuperTypeSelection.ALL, superConfig.getSelection());
        }

        /** @VALID @SPEC(07-supertype.md) Tests SINGLE supertype selection. */
        @Test
        @DisplayName("SINGLE supertype selection")
        void validConfig_singleSuperTypeStrategy_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithSingleSuperType");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getSuperTypeConfig());
            assertEquals(SuperTypeSelection.SINGLE, aspect.getSuperTypeConfig().getSelection());
        }

        /** @VALID @SPEC(07-supertype.md) Tests asArray=false. */
        @Test
        @DisplayName("asArray=false parsed correctly")
        void validConfig_superTypeAsArrayFalse_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithSuperTypesNotAsArray");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getSuperTypeConfig());
            SuperTypeSerializationConfig superConfig = aspect.getSuperTypeConfig();
            assertTrue(superConfig.isEnabled());
            assertFalse(superConfig.isAsArray());
        }

        /** @VALID @SPEC(07-supertype.md) Tests custom separator. */
        @Test
        @DisplayName("custom separator parsed correctly")
        void validConfig_superTypeSeparator_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithSuperTypesSeparator");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getSuperTypeConfig());
            SuperTypeSerializationConfig superConfig = aspect.getSuperTypeConfig();
            assertTrue(superConfig.isEnabled());
            assertFalse(superConfig.isAsArray());
            assertEquals("|", superConfig.getSeparator());
        }

        /** @VALID @SPEC(07-supertype.md) Tests default asArray=true when not specified. */
        @Test
        @DisplayName("default asArray=true when not specified")
        void validConfig_superTypeDefaultAsArrayTrue_defaultApplied() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithSuperTypes");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getSuperTypeConfig());
            SuperTypeSerializationConfig superConfig = aspect.getSuperTypeConfig();
            assertTrue(superConfig.isAsArray());
        }

        /** @VALID @SPEC(07-supertype.md) Tests default separator when asArray=false. */
        @Test
        @DisplayName("default separator when asArray=false")
        void validConfig_superTypeDefaultSeparator_defaultApplied() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithSuperTypesNotAsArray");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getSuperTypeConfig());
            SuperTypeSerializationConfig superConfig = aspect.getSuperTypeConfig();
            assertEquals(",", superConfig.getSeparator());
        }

        /** @VALID @SPEC(07-supertype.md) Tests structured supertype config. */
        @Test
        @DisplayName("structured supertype config")
        void validConfig_structuredSuperTypeConfig_allFieldsParsed() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithStructuredSuperType");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getSuperTypeConfig());
            SuperTypeSerializationConfig superConfig = aspect.getSuperTypeConfig();

            assertTrue(superConfig.isEnabled());
            assertEquals(SerializationFormat.STRUCTURED, superConfig.getFormat());
            // Note: schemaKey removed from SuperTypeConfig - codec uses TypeConfig.getSchemaKey()
            // Note: nameKey removed - superTypeKey has format-dependent default
            assertEquals("typeName", superConfig.getSuperTypeKey());
        }
    }

    // ========================================================================
    // Discriminator and Fallback Tests
    // ========================================================================

    @Nested
    @DisplayName("Discriminator and Fallback Configuration")
    class DiscriminatorFallbackTests {

        /** @VALID @SPEC(08-discriminator-mapping.md) Tests discriminator value. */
        @Test
        @DisplayName("discriminator value parsed correctly")
        void validConfig_discriminatorValue_parsedCorrectly() {
            EClass deviceClass = EcoreHelper.getEClass(testPackage, "DraginoDevice");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(deviceClass));

            assertEquals("Dragino_LSE01", aspect.getDiscriminatorValue());
        }

        /** @VALID @SPEC(08-discriminator-mapping.md) Tests fallback ERROR strategy typeMapping annotation. */
        @Test
        @DisplayName("typeMapping with fallback ERROR - discriminatorPath and mapId parsed")
        void validConfig_fallbackError_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithFallbackError");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getTypeConfig());
            TypeSerializationConfig typeConfig = aspect.getTypeConfig();

            assertEquals(TypeStrategy.NAME, typeConfig.getStrategy());
            assertEquals("deviceType", typeConfig.getDiscriminatorPath());
            assertEquals("fallback-error-test", typeConfig.getMapId());
            // fallbackStrategy is now handled by TypeDiscriminatorService, not stored on TypeSerializationConfig
        }

        /** @VALID @SPEC(08-discriminator-mapping.md) Tests fallback SKIP strategy typeMapping annotation. */
        @Test
        @DisplayName("typeMapping with fallback SKIP - discriminatorPath and mapId parsed")
        void validConfig_fallbackSkip_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithFallbackSkip");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getTypeConfig());
            TypeSerializationConfig typeConfig = aspect.getTypeConfig();

            assertEquals(TypeStrategy.NAME, typeConfig.getStrategy());
            assertEquals("deviceType", typeConfig.getDiscriminatorPath());
            assertEquals("fallback-skip-test", typeConfig.getMapId());
            // fallbackStrategy is now handled by TypeDiscriminatorService, not stored on TypeSerializationConfig
        }

        /** @VALID @SPEC(08-discriminator-mapping.md) Tests explicit fallback EClass typeMapping annotation. */
        @Test
        @DisplayName("typeMapping with explicit fallback - discriminatorPath and mapId parsed")
        void validConfig_explicitFallbackEClass_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithExplicitFallback");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getTypeConfig());
            TypeSerializationConfig typeConfig = aspect.getTypeConfig();

            assertEquals(TypeStrategy.NAME, typeConfig.getStrategy());
            assertEquals("deviceType", typeConfig.getDiscriminatorPath());
            assertEquals("fallback-explicit-test", typeConfig.getMapId());
            // fallbackStrategy and fallbackEClass are now handled by TypeDiscriminatorService
        }
    }

    // ========================================================================
    // Feature Configuration Tests
    // ========================================================================

    @Nested
    @DisplayName("Feature Configuration")
    class FeatureConfigTests {

        /** @VALID @SPEC(11-feature.md) Tests ignore=true on attribute. */
        @Test
        @DisplayName("ignore=true on attribute sets ignore=true")
        void validConfig_attributeIgnore_ignoreTrue() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithTransientField");
            EAttribute secretAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "secretData");

            FeatureCodecAspect aspect = (FeatureCodecAspect) provider.buildAttributeAspect(wrapAttribute(secretAttr));

            assertTrue(aspect.isIgnore());
        }

        /** @VALID @SPEC(11-feature.md) Tests attribute without ignore is not ignored. */
        @Test
        @DisplayName("attribute without ignore is not ignored")
        void validConfig_attributeNoIgnore_ignoreFalse() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithTransientField");
            EAttribute publicAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "publicData");

            FeatureCodecAspect aspect = (FeatureCodecAspect) provider.buildAttributeAspect(wrapAttribute(publicAttr));

            assertFalse(aspect.isIgnore());
        }

        /** @VALID @SPEC(11-feature.md) Tests ignore=true on reference. */
        @Test
        @DisplayName("ignore=true on reference sets ignore=true")
        void validConfig_referenceIgnore_ignoreTrue() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithTransientReference");
            EReference cachedRef = (EReference) EcoreHelper.getFeature(entityClass, "cachedAddress");

            ReferenceCodecAspect aspect = (ReferenceCodecAspect) provider.buildReferenceAspect(wrapReference(cachedRef));

            assertTrue(aspect.isIgnore());
        }

        /** @VALID @SPEC(11-feature.md) Tests explicit ignore=false keeps ignore=false. */
        @Test
        @DisplayName("explicit ignore=false keeps ignore=false")
        void validConfig_explicitIgnoreFalse_ignoreFalse() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithSerializeOptions");
            EAttribute attr = (EAttribute) EcoreHelper.getFeature(entityClass, "explicitSerialize");

            FeatureCodecAspect aspect = (FeatureCodecAspect) provider.buildAttributeAspect(wrapAttribute(attr));

            assertFalse(aspect.isIgnore());
        }

        /** @VALID @SPEC(11-feature.md) Tests ignore=true. */
        @Test
        @DisplayName("ignore=true sets ignore=true")
        void validConfig_ignoreTrue_ignoreTrue() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithSerializeOptions");
            EAttribute attr = (EAttribute) EcoreHelper.getFeature(entityClass, "noSerialize");

            FeatureCodecAspect aspect = (FeatureCodecAspect) provider.buildAttributeAspect(wrapAttribute(attr));

            assertTrue(aspect.isIgnore());
        }

        /** @VALID @SPEC(11-feature.md) Tests explicit ignore=false. */
        @Test
        @DisplayName("explicit ignore=false keeps ignore=false")
        void validConfig_explicitIgnoreFalse_ignoreFalse2() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithSerializeOptions");
            EAttribute attr = (EAttribute) EcoreHelper.getFeature(entityClass, "conflictSerializeWins");

            FeatureCodecAspect aspect = (FeatureCodecAspect) provider.buildAttributeAspect(wrapAttribute(attr));

            assertFalse(aspect.isIgnore());
        }

        /** @VALID @SPEC(11-feature.md) Tests serializeNull=true. */
        @Test
        @DisplayName("serializeNull=true")
        void validConfig_serializeNull_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithSerializeOptions");
            EAttribute attr = (EAttribute) EcoreHelper.getFeature(entityClass, "nullableField");

            FeatureCodecAspect aspect = (FeatureCodecAspect) provider.buildAttributeAspect(wrapAttribute(attr));

            assertTrue(aspect.isSerializeNull());
        }

        /** @VALID @SPEC(11-feature.md) Tests serializeEmpty=true. */
        @Test
        @DisplayName("serializeEmpty=true")
        void validConfig_serializeEmpty_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithSerializeOptions");
            EAttribute attr = (EAttribute) EcoreHelper.getFeature(entityClass, "emptyListField");

            FeatureCodecAspect aspect = (FeatureCodecAspect) provider.buildAttributeAspect(wrapAttribute(attr));

            assertTrue(aspect.isSerializeEmpty());
        }

        /** @VALID @SPEC(11-feature.md) Tests serializeDefaults=true. */
        @Test
        @DisplayName("serializeDefaults=true")
        void validConfig_serializeDefaults_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithSerializeOptions");
            EAttribute attr = (EAttribute) EcoreHelper.getFeature(entityClass, "defaultValueField");

            FeatureCodecAspect aspect = (FeatureCodecAspect) provider.buildAttributeAspect(wrapAttribute(attr));

            assertTrue(aspect.isSerializeDefaults());
        }

        /** @VALID @SPEC(11-feature.md) Tests custom key on attribute. */
        @Test
        @DisplayName("custom key on attribute")
        void validConfig_attributeCustomKey_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithCustomKeys");
            EAttribute firstNameAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "firstName");
            EAttribute lastNameAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "lastName");

            FeatureCodecAspect firstNameAspect = (FeatureCodecAspect) provider.buildAttributeAspect(wrapAttribute(firstNameAttr));
            FeatureCodecAspect lastNameAspect = (FeatureCodecAspect) provider.buildAttributeAspect(wrapAttribute(lastNameAttr));

            assertEquals("first_name", firstNameAspect.getEffectiveKey());
            assertEquals("last_name", lastNameAspect.getEffectiveKey());
        }

        /** @VALID @SPEC(11-feature.md) Tests custom key on reference. */
        @Test
        @DisplayName("custom key on reference")
        void validConfig_referenceCustomKey_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithCustomKeys");
            EReference homeAddressRef = (EReference) EcoreHelper.getFeature(entityClass, "homeAddress");

            ReferenceCodecAspect aspect = (ReferenceCodecAspect) provider.buildReferenceAspect(wrapReference(homeAddressRef));

            assertEquals("home_address", aspect.getEffectiveKey());
        }
    }

    // ========================================================================
    // Value Reader/Writer Tests
    // ========================================================================

    @Nested
    @DisplayName("Value Reader/Writer Configuration")
    class ValueHandlerTests {

        /** @VALID @SPEC(14-custom-values.md) Tests valueWriterName only. */
        @Test
        @DisplayName("valueWriterName only")
        void validConfig_valueWriterOnly_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithCustomValueHandlers");
            EAttribute attr = (EAttribute) EcoreHelper.getFeature(entityClass, "onlyWriter");

            FeatureCodecAspect aspect = (FeatureCodecAspect) provider.buildAttributeAspect(wrapAttribute(attr));

            assertEquals("CustomWriter", aspect.getValueWriterName());
            assertNull(aspect.getValueReaderName());
        }

        /** @VALID @SPEC(14-custom-values.md) Tests valueReaderName only. */
        @Test
        @DisplayName("valueReaderName only")
        void validConfig_valueReaderOnly_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithCustomValueHandlers");
            EAttribute attr = (EAttribute) EcoreHelper.getFeature(entityClass, "onlyReader");

            FeatureCodecAspect aspect = (FeatureCodecAspect) provider.buildAttributeAspect(wrapAttribute(attr));

            assertEquals("CustomReader", aspect.getValueReaderName());
            assertNull(aspect.getValueWriterName());
        }

        /** @VALID @SPEC(14-custom-values.md) Tests both reader and writer. */
        @Test
        @DisplayName("both reader and writer")
        void validConfig_bothReaderAndWriter_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithCustomValueHandlers");
            EAttribute birthDateAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "birthDate");

            FeatureCodecAspect aspect = (FeatureCodecAspect) provider.buildAttributeAspect(wrapAttribute(birthDateAttr));

            assertEquals("ISO8601DateWriter", aspect.getValueWriterName());
            assertEquals("FlexibleDateReader", aspect.getValueReaderName());
        }
    }

    // ========================================================================
    // Enum Serialization Tests
    // ========================================================================

    @Nested
    @DisplayName("Enum Serialization Configuration")
    class EnumSerializationTests {

        /** @VALID @SPEC(11-feature.md) Tests LITERAL enum strategy. */
        @Test
        @DisplayName("LITERAL enum strategy")
        void validConfig_enumLiteral_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithEnumFields");
            EAttribute attr = (EAttribute) EcoreHelper.getFeature(entityClass, "statusLiteral");

            FeatureCodecAspect aspect = (FeatureCodecAspect) provider.buildAttributeAspect(wrapAttribute(attr));

            assertEquals(EnumSerializationStrategy.LITERAL, aspect.getEnumSerialization());
        }

        /** @VALID @SPEC(11-feature.md) Tests VALUE enum strategy. */
        @Test
        @DisplayName("VALUE enum strategy")
        void validConfig_enumValue_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithEnumFields");
            EAttribute attr = (EAttribute) EcoreHelper.getFeature(entityClass, "statusValue");

            FeatureCodecAspect aspect = (FeatureCodecAspect) provider.buildAttributeAspect(wrapAttribute(attr));

            assertEquals(EnumSerializationStrategy.VALUE, aspect.getEnumSerialization());
        }

        /** @VALID @SPEC(11-feature.md) Tests NAME enum strategy. */
        @Test
        @DisplayName("NAME enum strategy")
        void validConfig_enumName_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "EntityWithEnumFields");
            EAttribute attr = (EAttribute) EcoreHelper.getFeature(entityClass, "statusName");

            FeatureCodecAspect aspect = (FeatureCodecAspect) provider.buildAttributeAspect(wrapAttribute(attr));

            assertEquals(EnumSerializationStrategy.NAME, aspect.getEnumSerialization());
        }
    }

    // ========================================================================
    // Reference Configuration Tests
    // ========================================================================

    @Nested
    @DisplayName("Reference Configuration")
    class ReferenceConfigTests {

        /** @VALID @SPEC(10-reference.md) Tests type config on reference. */
        @Test
        @DisplayName("type config on reference")
        void validConfig_referenceTypeConfig_parsedCorrectly() {
            EClass personClass = EcoreHelper.getEClass(testPackage, "PersonWithTypedReference");
            EReference addressRef = (EReference) EcoreHelper.getFeature(personClass, "address");

            ReferenceCodecAspect aspect = (ReferenceCodecAspect) provider.buildReferenceAspect(wrapReference(addressRef));

            assertNotNull(aspect.getTypeConfig());
            assertEquals(TypeStrategy.NAME, aspect.getTypeConfig().getStrategy());
            assertEquals("_refType", aspect.getTypeConfig().getTypeKey());
        }

        /** @VALID @SPEC(10-reference.md) Tests full refConfig. */
        @Test
        @DisplayName("full refConfig with format/refKey/typeKey/expand")
        void validConfig_fullRefConfig_allFieldsParsed() {
            EClass personClass = EcoreHelper.getEClass(testPackage, "PersonWithRefConfig");
            EReference employerRef = (EReference) EcoreHelper.getFeature(personClass, "employer");

            ReferenceCodecAspect aspect = (ReferenceCodecAspect) provider.buildReferenceAspect(wrapReference(employerRef));

            assertNotNull(aspect.getReferenceConfig());
            ReferenceSerializationConfig refConfig = aspect.getReferenceConfig();

            assertEquals(SerializationFormat.STRUCTURED, refConfig.getFormat());
            assertEquals("$ref", refConfig.getRefKey());
            assertEquals("$type", refConfig.getTypeKey());
            assertTrue(refConfig.isExpand());
            assertTrue(aspect.isExpand());
        }

        /**
         * @VALID @SPEC(08-discriminator-mapping.md) Tests inlineMapping source on reference.
         * Inline mappings are now handled by TypeDiscriminatorService, not stored on ReferenceCodecAspect.
         * The CodecAspectProvider only parses the codec annotation; inlineMapping source is processed separately.
         */
        @Test
        @DisplayName("inlineMapping source on reference - codec annotation parsed, diagnostics for misconfig")
        void validConfig_inlineMappingSource_codecAnnotationParsed() {
            EClass personClass = EcoreHelper.getEClass(testPackage, "PersonWithContacts");
            EReference contactsRef = (EReference) EcoreHelper.getFeature(personClass, "contacts");

            ReferenceCodecAspect aspect = (ReferenceCodecAspect) provider.buildReferenceAspect(wrapReference(contactsRef));

            // typeDiscriminatorPath in codec annotation is class-only → should generate diagnostic
            assertNull(aspect.getTypeConfig(),
                "typeConfig should be null - typeDiscriminatorPath is class-only and should be ignored on EReference");
            assertEquals(1, aspect.getDiagnostics().size(),
                "Should have one diagnostic for ignored typeDiscriminatorPath");
            // Inline mappings are no longer stored on the aspect - they are handled by TypeDiscriminatorService
        }

        /**
         * @VALID @SPEC(08-discriminator-mapping.md) Tests inlineMapping + fallback source on reference.
         * Inline mappings and fallback are now handled by TypeDiscriminatorService, not stored on ReferenceCodecAspect.
         */
        @Test
        @DisplayName("inlineMapping with fallback on reference - codec annotation parsed, diagnostics for misconfig")
        void validConfig_referenceFallbackConfig_codecAnnotationParsed() {
            EClass personClass = EcoreHelper.getEClass(testPackage, "PersonWithFallbackReference");
            EReference contactsRef = (EReference) EcoreHelper.getFeature(personClass, "contacts");

            ReferenceCodecAspect aspect = (ReferenceCodecAspect) provider.buildReferenceAspect(wrapReference(contactsRef));

            // typeDiscriminatorPath in codec annotation is class-only → should generate diagnostic
            assertNull(aspect.getTypeConfig(),
                "typeConfig should be null - typeDiscriminatorPath is class-only and should be ignored");
            assertEquals(1, aspect.getDiagnostics().size(),
                "Should have one diagnostic for ignored typeDiscriminatorPath");
            // Inline mappings and fallback are no longer stored on the aspect - handled by TypeDiscriminatorService
        }
    }

    // ========================================================================
    // Configuration Hierarchy Tests
    // ========================================================================

    @Nested
    @DisplayName("Configuration Hierarchy")
    class HierarchyTests {

        /** @VALID Tests reference without override has no type config (inheritance at runtime). */
        @Test
        @DisplayName("reference without override has no type config")
        void validConfig_referenceNoOverride_noTypeConfig() {
            EClass personClass = EcoreHelper.getEClass(testPackage, "PersonWithHierarchyConfig");
            EReference primaryAddressRef = (EReference) EcoreHelper.getFeature(personClass, "primaryAddress");

            ReferenceCodecAspect aspect = (ReferenceCodecAspect) provider.buildReferenceAspect(wrapReference(primaryAddressRef));

            assertNull(aspect.getTypeConfig());
        }

        /** @VALID Tests reference with full override. */
        @Test
        @DisplayName("reference overrides type config")
        void validConfig_referenceOverridesTypeConfig_parsedCorrectly() {
            EClass personClass = EcoreHelper.getEClass(testPackage, "PersonWithHierarchyConfig");
            EReference businessAddressRef = (EReference) EcoreHelper.getFeature(personClass, "businessAddress");

            ReferenceCodecAspect aspect = (ReferenceCodecAspect) provider.buildReferenceAspect(wrapReference(businessAddressRef));

            assertNotNull(aspect.getTypeConfig());
            TypeSerializationConfig typeConfig = aspect.getTypeConfig();

            assertEquals(TypeStrategy.NAME, typeConfig.getStrategy());
            assertEquals(SerializationFormat.STRUCTURED, typeConfig.getFormat());
            assertEquals("addressType", typeConfig.getTypeKey());
        }

        /** @VALID Tests reference with partial override. */
        @Test
        @DisplayName("reference partially overrides type config")
        void validConfig_referencePartiallyOverrides_parsedCorrectly() {
            EClass personClass = EcoreHelper.getEClass(testPackage, "PersonWithHierarchyConfig");
            EReference shippingAddressRef = (EReference) EcoreHelper.getFeature(personClass, "shippingAddress");

            ReferenceCodecAspect aspect = (ReferenceCodecAspect) provider.buildReferenceAspect(wrapReference(shippingAddressRef));

            assertNotNull(aspect.getTypeConfig());
            TypeSerializationConfig typeConfig = aspect.getTypeConfig();

            assertEquals(SerializationFormat.STRUCTURED, typeConfig.getFormat());
            assertEquals(TypeStrategy.URI, typeConfig.getStrategy()); // Default
            assertEquals("_type", typeConfig.getTypeKey()); // EMF model default
        }

        /** @VALID Tests class-level type config. */
        @Test
        @DisplayName("class-level type config")
        void validConfig_classTypeConfig_parsedCorrectly() {
            EClass personClass = EcoreHelper.getEClass(testPackage, "PersonWithHierarchyConfig");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(personClass));

            assertNotNull(aspect.getTypeConfig());
            TypeSerializationConfig typeConfig = aspect.getTypeConfig();

            assertEquals(TypeStrategy.URI, typeConfig.getStrategy());
            assertEquals(SerializationFormat.PLAIN, typeConfig.getFormat());
            assertEquals("_type", typeConfig.getTypeKey());
        }
    }

    // ========================================================================
    // Combined Annotations Tests
    // ========================================================================

    @Nested
    @DisplayName("Combined Annotations")
    class CombinedTests {

        /** @VALID Tests class with all config types in single annotation. */
        @Test
        @DisplayName("class with all config types")
        void validConfig_allConfigsCombined_allParsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "FullyConfiguredEntity");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertNotNull(aspect.getIdConfig());
            assertNotNull(aspect.getTypeConfig());
            assertNotNull(aspect.getSuperTypeConfig());
            assertTrue(aspect.isInheritFromParent());

            assertEquals(IdStrategy.ID_FIELD, aspect.getIdConfig().getStrategy());
            assertEquals(TypeStrategy.URI, aspect.getTypeConfig().getStrategy());
            assertTrue(aspect.getSuperTypeConfig().isEnabled());
        }

        /** @VALID Tests inherit annotation. */
        @Test
        @DisplayName("inherit annotation")
        void validConfig_inheritAnnotation_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "InheritingEntity");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertTrue(aspect.isInheritFromParent());
        }
    }

    // ========================================================================
    // Strictness Configuration (spec 11-feature.md §11)
    // ========================================================================

    @Nested
    @DisplayName("Strictness Configuration (spec §11)")
    class StrictnessConfig {

        /** @VALID Both strictOnUnknown and strictOnMissing on EClass. */
        @Test
        @DisplayName("both strictness flags set to true")
        void validConfig_bothStrictnessFlags_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "StrictEntity");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertTrue(aspect.isStrictOnUnknown(),
                "strictOnUnknown should be true");
            assertTrue(aspect.isStrictOnMissing(),
                "strictOnMissing should be true");
        }

        /** @VALID Only strictOnUnknown on EClass. */
        @Test
        @DisplayName("only strictOnUnknown set to true")
        void validConfig_strictOnUnknownOnly_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "StrictUnknownOnly");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertTrue(aspect.isStrictOnUnknown(),
                "strictOnUnknown should be true");
            assertFalse(aspect.isStrictOnMissing(),
                "strictOnMissing should remain false (not set)");
        }

        /** @VALID Only strictOnMissing on EClass. */
        @Test
        @DisplayName("only strictOnMissing set to true")
        void validConfig_strictOnMissingOnly_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "StrictMissingOnly");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertFalse(aspect.isStrictOnUnknown(),
                "strictOnUnknown should remain false (not set)");
            assertTrue(aspect.isStrictOnMissing(),
                "strictOnMissing should be true");
        }

        /** @VALID SimpleClass has no strictness flags - defaults apply. */
        @Test
        @DisplayName("default strictness flags are false")
        void validConfig_noStrictnessFlags_defaultsFalse() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "SimpleClass");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertFalse(aspect.isStrictOnUnknown(),
                "strictOnUnknown defaults to false");
            assertFalse(aspect.isStrictOnMissing(),
                "strictOnMissing defaults to false");
        }
    }

    // ========================================================================
    // Metadata Merge Config (spec 05-global-options.md §5)
    // ========================================================================

    @Nested
    @DisplayName("Metadata Merge Config")
    class MetadataMergeConfig {

        /** @VALID Both metadataMerge and metadataKey set on EClass. */
        @Test
        @DisplayName("metadataMerge=true with custom metadataKey")
        void validConfig_metadataMergeWithCustomKey_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "MergedMetadataEntity");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertTrue(aspect.isMetadataMerge(),
                "metadataMerge should be true");
            assertEquals("_meta", aspect.getMetadataKey(),
                "metadataKey should be '_meta'");
        }

        /** @VALID metadataMerge=true with default key on EClass. */
        @Test
        @DisplayName("metadataMerge=true with default metadataKey")
        void validConfig_metadataMergeDefaultKey_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "MergedMetadataDefaultKey");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertTrue(aspect.isMetadataMerge(),
                "metadataMerge should be true");
            assertEquals("_metadata", aspect.getMetadataKey(),
                "metadataKey should default to '_metadata'");
        }

        /** @VALID metadataMerge=false explicitly set. */
        @Test
        @DisplayName("metadataMerge=false explicitly set")
        void validConfig_metadataMergeDisabled_parsedCorrectly() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "MergedMetadataDisabled");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertFalse(aspect.isMetadataMerge(),
                "metadataMerge should be false");
        }

        /** @VALID SimpleClass has no metadata merge flags - defaults apply. */
        @Test
        @DisplayName("default metadata merge flags")
        void validConfig_noMetadataMergeFlags_defaultsFalse() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "SimpleClass");

            ClassCodecAspect aspect = (ClassCodecAspect) provider.buildClassAspect(wrapClass(entityClass));

            assertFalse(aspect.isMetadataMerge(),
                "metadataMerge defaults to false");
            assertEquals("_metadata", aspect.getMetadataKey(),
                "metadataKey defaults to '_metadata'");
        }
    }
}
