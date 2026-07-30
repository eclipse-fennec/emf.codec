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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.EnumSerializationStrategy;
import org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.model.metadata.AspectEntry;
import org.eclipse.fennec.emf.osgi.model.metadata.AttributeMetadata;
import org.eclipse.fennec.emf.osgi.model.metadata.ClassMetadata;
import org.eclipse.fennec.emf.osgi.model.metadata.DiagnosticSeverity;
import org.eclipse.fennec.emf.osgi.model.metadata.MetadataDiagnostic;
import org.eclipse.fennec.emf.osgi.model.metadata.MetadataFactory;
import org.eclipse.fennec.emf.osgi.model.metadata.PackageMetadata;
import org.eclipse.fennec.emf.osgi.model.metadata.ReferenceMetadata;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for misconfigured codec annotations (keys at wrong EMF levels).
 * <p>
 * These tests verify that annotation keys placed at wrong EMF element levels
 * are <b>ignored</b> and generate appropriate <b>diagnostics</b>.
 * </p>
 * <p>
 * Per Section 7.4 of 19-test-coverage.md, every misconfiguration test MUST verify:
 * <ol>
 *   <li>Value is NOT applied (null, not set)</li>
 *   <li>Diagnostic IS added with correct key</li>
 * </ol>
 * </p>
 * <p>
 * All test models use @MISCONFIG tags in test-codec-annotations.ecore.
 * </p>
 *
 * @see CodecAspectProvider
 * @see CodecAspectProviderValidConfigTest for valid configuration tests
 */
@DisplayName("CodecAspectProvider - Misconfiguration Tests")
class CodecAspectProviderMisconfigTest {

    private static final String TEST_ECORE = "test-codec-annotations.ecore";

    private CodecAspectProvider provider;
    private EcoreHelper helper;
    private EPackage testPackage;

    @BeforeEach
    void setUp() throws IOException {
        provider = new CodecAspectProvider();
        helper = new EcoreHelper();
        testPackage = helper.loadEcore(TEST_ECORE, CodecAspectProviderMisconfigTest.class);
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

    /**
     * Runs the provider over a package tree holding just this class (with its features) and
     * returns the codec aspect entry the provider hung on the class.
     * <p>
     * The provider no longer exposes per-element builders:
     * {@link CodecAspectProvider#onPackageRegistered(PackageMetadata)} is the single entry
     * point, and an aspect arrives inside an {@link AspectEntry} whose {@code content} is the
     * codec aspect and whose {@code diagnostics} carry the parse findings (issue #85, D2).
     * </p>
     */
    private AspectEntry classEntry(EClass eClass) {
        return buildTree(eClass).getAspects().get(0);
    }

    /** The codec aspect entry the provider hung on one feature of its owning class. */
    private AspectEntry featureEntry(EStructuralFeature feature) {
        return buildTree((EClass) feature.eContainer()).getFeatures().stream()
                .filter(featureMetadata -> featureMetadata.getEFeature() == feature)
                .findFirst()
                .orElseThrow(() -> new AssertionError("no metadata for feature " + feature.getName()))
                .getAspects().get(0);
    }

    private ClassMetadata buildTree(EClass eClass) {
        PackageMetadata packageMetadata = MetadataFactory.eINSTANCE.createPackageMetadata();
        packageMetadata.setEPackage(eClass.getEPackage());
        ClassMetadata classMetadata = wrapClass(eClass);
        packageMetadata.getClasses().add(classMetadata);
        for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
            classMetadata.getFeatures().add(feature instanceof EAttribute attribute
                    ? wrapAttribute(attribute)
                    : wrapReference((EReference) feature));
        }
        provider.onPackageRegistered(packageMetadata);
        return classMetadata;
    }

    // ========================================================================
    // Keys on Wrong Level: EClass
    // ========================================================================

    @Nested
    @DisplayName("Class-Level Misconfigurations")
    class ClassMisconfigTests {

        /**
         * @MISCONFIG @SPEC(10-reference.md#R-V1)
         * Tests that refFormat/refKey on EClass are ignored with WARNING diagnostics.
         * These keys are reference-only per spec.
         */
        @Test
        @DisplayName("refFormat/refKey on EClass - ignored with WARNING diagnostics (R-V1)")
        void misconfig_refConfigKeysOnClass_ignored() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "ClassWithRefConfigMisplaced");

            AspectEntry entry = classEntry(entityClass);
            ClassCodecAspect aspect = (ClassCodecAspect) entry.getContent();

            // Valid key was parsed
            assertNotNull(aspect.getTypeConfig());
            assertEquals(TypeStrategy.URI, aspect.getTypeConfig().getStrategy());

            // R-V1: ref* keys on EClass → WARNING
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> d.getKey().startsWith("ref")
                            && d.getSeverity() == DiagnosticSeverity.WARNING),
                "Should have WARNING diagnostic for ref* keys on EClass (R-V1)");
        }

        /**
         * @MISCONFIG @SPEC(08-discriminator-mapping.md)
         * Tests that inlineMapping.* keys on EClass are ignored.
         * These keys are reference-only per spec.
         */
        @Test
        @DisplayName("inlineMapping.* on EClass - ignored")
        void misconfig_inlineMappingKeysOnClass_ignored() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "ClassWithInlineMappingMisplaced");

            AspectEntry entry = classEntry(entityClass);
            ClassCodecAspect aspect = (ClassCodecAspect) entry.getContent();

            // Valid key was parsed
            assertNotNull(aspect.getTypeConfig());
            assertEquals(TypeStrategy.URI, aspect.getTypeConfig().getStrategy());

            // ClassCodecAspect doesn't have getInlineTypeMappings() - that's correct
            // The inlineMapping.* keys are simply ignored for classes
        }
    }

    // ========================================================================
    // Keys on Wrong Level: EReference
    // ========================================================================

    @Nested
    @DisplayName("Reference-Level Misconfigurations")
    class ReferenceMisconfigTests {

        /**
         * @MISCONFIG @SPEC(09-id.md#ID-V1, ID-V2)
         * Tests that idStrategy/idFeatures on EReference are ignored with ERROR diagnostics.
         * These keys are class-only per spec.
         */
        @Test
        @DisplayName("idStrategy/idFeatures on EReference - ignored with ERROR diagnostics")
        void misconfig_idConfigKeysOnReference_ignored() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "RefWithIdConfigMisplaced");
            EReference addressRef = (EReference) EcoreHelper.getFeature(entityClass, "address");

            AspectEntry entry = featureEntry(addressRef);
            ReferenceCodecAspect aspect = (ReferenceCodecAspect) entry.getContent();

            // Valid key was parsed
            assertNotNull(aspect.getTypeConfig());
            assertEquals(TypeStrategy.NAME, aspect.getTypeConfig().getStrategy());

            // ID-V1/ID-V2: idStrategy/idFeatures on EReference → ERROR
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "idStrategy".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.ERROR),
                "Should have ERROR diagnostic for idStrategy on EReference (ID-V1)");
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "idFeatures".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.ERROR),
                "Should have ERROR diagnostic for idFeatures on EReference (ID-V2)");
        }

        /**
         * @MISCONFIG @SPEC(16-annotation-reference.md, ST-V1)
         * Tests that superType* keys on EReference are ignored with ERROR diagnostics.
         * SuperType is class-intrinsic, not reference-specific.
         */
        @Test
        @DisplayName("superType* on EReference - ignored with ERROR diagnostics (ST-V1)")
        void misconfig_superTypeConfigKeysOnReference_ignored() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "RefWithSuperTypeMisplaced");
            EReference addressRef = (EReference) EcoreHelper.getFeature(entityClass, "address");

            AspectEntry entry = featureEntry(addressRef);
            ReferenceCodecAspect aspect = (ReferenceCodecAspect) entry.getContent();

            // Valid key was parsed
            assertEquals("addr", aspect.getEffectiveKey());

            // ST-V1: superType* keys on EReference → ERROR diagnostics
            assertTrue(entry.getDiagnostics().size() >= 1,
                "Should have at least 1 ERROR diagnostic for superType* keys on EReference");
            for (MetadataDiagnostic d : entry.getDiagnostics()) {
                assertEquals(DiagnosticSeverity.ERROR, d.getSeverity(),
                    "All superType* key diagnostics on EReference should be ERROR severity");
                assertTrue(d.getKey().startsWith("superType"),
                    "Diagnostic key should start with 'superType', got: " + d.getKey());
            }
        }

        /**
         * @MISCONFIG @SPEC(11-feature.md)
         * Tests that enumSerialization on EReference is ignored.
         * This key is attribute-only per spec.
         */
        @Test
        @DisplayName("enumSerialization on EReference - ignored")
        void misconfig_enumSerializationOnReference_ignored() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "RefWithEnumSerializationMisplaced");
            EReference addressRef = (EReference) EcoreHelper.getFeature(entityClass, "address");

            AspectEntry entry = featureEntry(addressRef);
            ReferenceCodecAspect aspect = (ReferenceCodecAspect) entry.getContent();

            // Valid key was parsed
            assertEquals("addr", aspect.getEffectiveKey());

            // The annotation has VALUE but should be ignored - stays at EMF model default (LITERAL)
            assertEquals(EnumSerializationStrategy.LITERAL, aspect.getEnumSerialization());
        }

        /**
         * @MISCONFIG @SPEC(08-discriminator-mapping.md, 06-type.md#T-V7)
         * Tests that typeMapping/{mapId} source on EReference is ignored with ERROR diagnostic.
         * typeMapping is class-only per spec.
         */
        @Test
        @DisplayName("typeMapping source on EReference - ignored with ERROR diagnostic (T-V7)")
        void misconfig_typeMappingOnReference_ignored() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "RefWithDiscriminatorMisplaced");
            EReference addressRef = (EReference) EcoreHelper.getFeature(entityClass, "address");

            AspectEntry entry = featureEntry(addressRef);
            ReferenceCodecAspect aspect = (ReferenceCodecAspect) entry.getContent();

            // Valid key was parsed
            assertNotNull(aspect.getTypeConfig());
            assertEquals(TypeStrategy.NAME, aspect.getTypeConfig().getStrategy());

            // typeMapping source on EReference → ERROR diagnostic
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> d.getSeverity() == DiagnosticSeverity.ERROR
                            && d.getMessage().contains("typeMapping")),
                "Should have ERROR diagnostic for typeMapping source on EReference");
        }

        /**
         * @MISCONFIG @SPEC(08-discriminator-mapping.md#7)
         * Tests that typeDiscriminatorPath on EReference is ignored with diagnostic.
         * This key is class-only per spec section 7.
         */
        @Test
        @DisplayName("typeDiscriminatorPath on EReference - ignored with diagnostic")
        void misconfig_typeDiscriminatorPathOnReference_ignoredWithDiagnostic() {
            EClass personClass = EcoreHelper.getEClass(testPackage, "PersonWithContacts");
            EReference contactsRef = (EReference) EcoreHelper.getFeature(personClass, "contacts");

            AspectEntry entry = featureEntry(contactsRef);
            ReferenceCodecAspect aspect = (ReferenceCodecAspect) entry.getContent();

            // 1. Value is NOT applied - typeConfig should be null
            assertNull(aspect.getTypeConfig(),
                "typeConfig should be null - typeDiscriminatorPath is class-only and should be ignored on EReference");

            // 2. Diagnostic IS added
            assertEquals(1, entry.getDiagnostics().size(),
                "Should have one diagnostic for ignored typeDiscriminatorPath");
            assertEquals("typeDiscriminatorPath", entry.getDiagnostics().get(0).getKey());

            // Inline mappings are now handled by TypeDiscriminatorService via dedicated inlineMapping source,
            // not stored on ReferenceCodecAspect
        }

        /**
         * @MISCONFIG @SPEC(08-discriminator-mapping.md)
         * Tests that typeMapping/{mapId} source on EReference is ignored with ERROR diagnostic.
         * typeMapping is class-only per spec.
         */
        @Test
        @DisplayName("typeMapping source on EReference - ignored with ERROR diagnostic")
        void misconfig_typeMappingSourceOnReference_ignored() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "RefWithTypeMapIdMisplaced");
            EReference addressRef = (EReference) EcoreHelper.getFeature(entityClass, "address");

            AspectEntry entry = featureEntry(addressRef);
            ReferenceCodecAspect aspect = (ReferenceCodecAspect) entry.getContent();

            // Valid key was parsed
            assertNotNull(aspect.getTypeConfig());
            assertEquals(TypeStrategy.NAME, aspect.getTypeConfig().getStrategy());

            // typeMapping source on EReference → ERROR diagnostic
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> d.getSeverity() == DiagnosticSeverity.ERROR
                            && d.getMessage().contains("typeMapping")),
                "Should have ERROR diagnostic for typeMapping source on EReference");
        }

        /**
         * @MISCONFIG @SPEC(09-id.md#ID-V3 through ID-V8)
         * Tests that additional class-only id* keys on EReference are ignored with ERROR diagnostics.
         * idSeparator, idKeyMode, idOnTop, idValueKey are class-only.
         * Note: idFormat and idKey ARE valid on EReference per spec.
         */
        @Test
        @DisplayName("more id* class-only keys on EReference - ignored with ERROR diagnostics")
        void misconfig_moreIdConfigKeysOnReference_ignored() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "RefWithMoreIdConfigMisplaced");
            EReference addressRef = (EReference) EcoreHelper.getFeature(entityClass, "address");

            AspectEntry entry = featureEntry(addressRef);
            ReferenceCodecAspect aspect = (ReferenceCodecAspect) entry.getContent();

            // Valid key was parsed
            assertEquals("addr", aspect.getEffectiveKey());

            // Class-only id* keys → ERROR diagnostics
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> d.getKey().startsWith("id")
                            && d.getSeverity() == DiagnosticSeverity.ERROR),
                "Should have at least 1 ERROR diagnostic for class-only id* keys on EReference");
        }

        /**
         * @MISCONFIG @SPEC(08-discriminator-mapping.md#7)
         * Tests fallback reference with typeDiscriminatorPath ignored but other valid keys parsed.
         */
        @Test
        @DisplayName("fallback reference with ignored typeDiscriminatorPath")
        void misconfig_fallbackReferenceWithIgnoredPath_validKeysParsed() {
            EClass personClass = EcoreHelper.getEClass(testPackage, "PersonWithFallbackReference");
            EReference contactsRef = (EReference) EcoreHelper.getFeature(personClass, "contacts");

            AspectEntry entry = featureEntry(contactsRef);
            ReferenceCodecAspect aspect = (ReferenceCodecAspect) entry.getContent();

            // 1. typeDiscriminatorPath is class-only - should be IGNORED
            assertNull(aspect.getTypeConfig(),
                "typeConfig should be null - typeDiscriminatorPath is class-only and should be ignored");

            // 2. Should have diagnostic warning for the ignored key
            assertEquals(1, entry.getDiagnostics().size(),
                "Should have one diagnostic for ignored typeDiscriminatorPath");
            assertEquals("typeDiscriminatorPath", entry.getDiagnostics().get(0).getKey());

            // 3. Inline mappings and fallback are now handled by TypeDiscriminatorService
            //    via dedicated inlineMapping source, not stored on ReferenceCodecAspect
        }
    }

    // ========================================================================
    // Keys on Wrong Level: EAttribute
    // ========================================================================

    @Nested
    @DisplayName("Attribute-Level Misconfigurations")
    class AttributeMisconfigTests {

        /**
         * @MISCONFIG @SPEC(06-type.md)
         * Tests that type* keys on EAttribute are ignored.
         * These keys are class/reference-only per spec.
         */
        @Test
        @DisplayName("type* keys on EAttribute - ignored")
        void misconfig_typeConfigKeysOnAttribute_ignored() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "AttrWithTypeConfigMisplaced");
            EAttribute nameAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "name");

            AspectEntry entry = featureEntry(nameAttr);
            FeatureCodecAspect aspect = (FeatureCodecAspect) entry.getContent();

            // Valid key was parsed
            assertEquals("attr_name", aspect.getEffectiveKey());

            // FeatureCodecAspect doesn't have getTypeConfig() - that's correct
            // The type* keys are simply ignored for attributes
        }

        /**
         * @MISCONFIG @SPEC(16-annotation-reference.md, ST-V2)
         * Tests that superType* keys on EAttribute are ignored with ERROR diagnostics.
         * SuperType is not applicable to attributes.
         */
        @Test
        @DisplayName("superType* keys on EAttribute - ignored with ERROR diagnostics (ST-V2)")
        void misconfig_superTypeConfigKeysOnAttribute_ignored() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "AttrWithSuperTypeMisplaced");
            EAttribute nameAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "name");

            AspectEntry entry = featureEntry(nameAttr);
            FeatureCodecAspect aspect = (FeatureCodecAspect) entry.getContent();

            // Valid key was parsed
            assertEquals("attr_name", aspect.getEffectiveKey());

            // ST-V2: superType* keys on EAttribute → ERROR diagnostics
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> d.getKey().startsWith("superType")
                            && d.getSeverity() == DiagnosticSeverity.ERROR),
                "Should have at least 1 ERROR diagnostic for superType* keys on EAttribute");
        }

        /**
         * @MISCONFIG @SPEC(09-id.md#ID-V13)
         * Tests that id* keys on EAttribute are ignored with ERROR diagnostics.
         * ID configuration is not applicable to attributes.
         */
        @Test
        @DisplayName("id* keys on EAttribute - ignored with ERROR diagnostics (ID-V13)")
        void misconfig_idConfigKeysOnAttribute_ignored() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "AttrWithIdConfigMisplaced");
            EAttribute nameAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "name");

            AspectEntry entry = featureEntry(nameAttr);
            FeatureCodecAspect aspect = (FeatureCodecAspect) entry.getContent();

            // Valid key was parsed
            assertEquals("attr_name", aspect.getEffectiveKey());

            // ID-V13: id* keys on EAttribute → ERROR
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> d.getKey().startsWith("id")
                            && d.getSeverity() == DiagnosticSeverity.ERROR),
                "Should have at least 1 ERROR diagnostic for id* keys on EAttribute");
        }

        /**
         * @MISCONFIG @SPEC(10-reference.md#R-V2)
         * Tests that ref* keys on EAttribute are ignored with ERROR diagnostics.
         * These keys are reference-only per spec.
         */
        @Test
        @DisplayName("ref* keys on EAttribute - ignored with ERROR diagnostics (R-V2)")
        void misconfig_refConfigKeysOnAttribute_ignored() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "AttrWithRefConfigMisplaced");
            EAttribute nameAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "name");

            AspectEntry entry = featureEntry(nameAttr);
            FeatureCodecAspect aspect = (FeatureCodecAspect) entry.getContent();

            // Valid key was parsed
            assertEquals("attr_name", aspect.getEffectiveKey());

            // R-V2: ref* keys on EAttribute → ERROR
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> d.getKey().startsWith("ref")
                            && d.getSeverity() == DiagnosticSeverity.ERROR),
                "Should have ERROR diagnostic for ref* keys on EAttribute (R-V2)");
        }

        /**
         * @MISCONFIG @SPEC(08-discriminator-mapping.md)
         * Tests that inlineMapping.* keys on EAttribute are ignored.
         * These keys are reference-only per spec.
         */
        @Test
        @DisplayName("inlineMapping.* on EAttribute - ignored")
        void misconfig_inlineMappingKeysOnAttribute_ignored() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "AttrWithInlineMappingMisplaced");
            EAttribute nameAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "name");

            AspectEntry entry = featureEntry(nameAttr);
            FeatureCodecAspect aspect = (FeatureCodecAspect) entry.getContent();

            // Valid key was parsed
            assertEquals("attr_name", aspect.getEffectiveKey());

            // FeatureCodecAspect doesn't have getInlineTypeMappings() - that's correct
            // The inlineMapping.* keys are simply ignored for attributes
        }

        /**
         * @MISCONFIG @SPEC(08-discriminator-mapping.md)
         * Tests that fallback* keys on EAttribute are ignored.
         * These keys are class/reference-only per spec.
         */
        @Test
        @DisplayName("fallback* keys on EAttribute - ignored")
        void misconfig_fallbackConfigKeysOnAttribute_ignored() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "AttrWithFallbackMisplaced");
            EAttribute nameAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "name");

            AspectEntry entry = featureEntry(nameAttr);
            FeatureCodecAspect aspect = (FeatureCodecAspect) entry.getContent();

            // Valid key was parsed
            assertEquals("attr_name", aspect.getEffectiveKey());

            // FeatureCodecAspect doesn't have getFallbackStrategy()/getFallbackEClass() - that's correct
            // The fallback* keys are simply ignored for attributes
        }

        /**
         * @MISCONFIG @SPEC(10-reference.md#R-V4)
         * Tests that expand on EAttribute is ignored with ERROR diagnostic.
         * This key is reference-only per spec.
         */
        @Test
        @DisplayName("expand on EAttribute - ignored with ERROR diagnostic (R-V4)")
        void misconfig_expandOnAttribute_ignored() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "AttrWithExpandMisplaced");
            EAttribute nameAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "name");

            AspectEntry entry = featureEntry(nameAttr);
            FeatureCodecAspect aspect = (FeatureCodecAspect) entry.getContent();

            // Valid key was parsed
            assertEquals("attr_name", aspect.getEffectiveKey());

            // R-V4: expand on EAttribute → ERROR
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "expand".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.ERROR),
                "Should have ERROR diagnostic for expand on EAttribute (R-V4)");
        }

        /**
         * @MISCONFIG @SPEC(06-type.md#T-V1, T-V2)
         * Tests that typeValueReaderName/WriterName on EReference are ignored with ERROR diagnostics.
         * These keys are class-intrinsic per spec.
         */
        @Test
        @DisplayName("typeValueReaderName/WriterName on EReference - ignored with ERROR diagnostic (T-V1, T-V2)")
        void misconfig_typeValueHandlersOnReference_ignoredWithDiagnostic() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "RefWithTypeValueHandlersMisplaced");
            EReference addressRef = (EReference) EcoreHelper.getFeature(entityClass, "address");

            AspectEntry entry = featureEntry(addressRef);
            ReferenceCodecAspect aspect = (ReferenceCodecAspect) entry.getContent();

            // Valid key was parsed
            assertEquals("addr", aspect.getEffectiveKey());

            // T-V1: typeValueReaderName on EReference → ERROR
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "typeValueReaderName".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.ERROR),
                "Should have ERROR diagnostic for typeValueReaderName on EReference (T-V1)");

            // T-V2: typeValueWriterName on EReference → ERROR
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "typeValueWriterName".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.ERROR),
                "Should have ERROR diagnostic for typeValueWriterName on EReference (T-V2)");

            // Should have exactly 2 diagnostics
            assertEquals(2, entry.getDiagnostics().size(),
                "Should have exactly 2 ERROR diagnostics for typeValueReaderName and typeValueWriterName");
        }
    }

    // ========================================================================
    // Layer 1 Annotation Validation Tests (06-type.md section 7)
    // ========================================================================

    @Nested
    @DisplayName("Layer 1 Annotation Validation - Type Config Rules")
    class Layer1ValidationTests {

        // ---- T-V3/T-V4: Runtime-only keys in EAnnotation ----

        /**
         * @MISCONFIG @SPEC(06-type.md#T-V3, T-V4)
         * Tests that typeScope and typeFormatScope on EClass generate WARNING diagnostics.
         * These are runtime-only properties, not valid in EAnnotations.
         */
        @Test
        @DisplayName("T-V3/T-V4: typeScope/typeFormatScope on EClass - WARNING diagnostics")
        void validation_runtimeOnlyKeysOnClass_warningDiagnostics() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "ClassWithRuntimeOnlyKeys");

            AspectEntry entry = classEntry(entityClass);
            ClassCodecAspect aspect = (ClassCodecAspect) entry.getContent();

            // Valid key was parsed
            assertNotNull(aspect.getTypeConfig());
            assertEquals(TypeStrategy.URI, aspect.getTypeConfig().getStrategy());

            // T-V3: typeScope → WARNING
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "typeScope".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.WARNING),
                "Should have WARNING diagnostic for typeScope in EAnnotation (T-V3)");

            // T-V4: typeFormatScope → WARNING
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "typeFormatScope".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.WARNING),
                "Should have WARNING diagnostic for typeFormatScope in EAnnotation (T-V4)");

            assertEquals(2, entry.getDiagnostics().size(),
                "Should have exactly 2 WARNING diagnostics for runtime-only keys");
        }

        /**
         * @MISCONFIG @SPEC(06-type.md#T-V3, T-V4)
         * Tests that typeScope and typeFormatScope on EReference generate WARNING diagnostics.
         */
        @Test
        @DisplayName("T-V3/T-V4: typeScope/typeFormatScope on EReference - WARNING diagnostics")
        void validation_runtimeOnlyKeysOnReference_warningDiagnostics() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "RefWithRuntimeOnlyKeys");
            EReference addressRef = (EReference) EcoreHelper.getFeature(entityClass, "address");

            AspectEntry entry = featureEntry(addressRef);
            ReferenceCodecAspect aspect = (ReferenceCodecAspect) entry.getContent();

            // Valid key was parsed
            assertNotNull(aspect.getTypeConfig());
            assertEquals(TypeStrategy.NAME, aspect.getTypeConfig().getStrategy());

            // T-V3: typeScope → WARNING
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "typeScope".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.WARNING),
                "Should have WARNING diagnostic for typeScope in EAnnotation (T-V3)");

            // T-V4: typeFormatScope → WARNING
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "typeFormatScope".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.WARNING),
                "Should have WARNING diagnostic for typeFormatScope in EAnnotation (T-V4)");

            assertEquals(2, entry.getDiagnostics().size(),
                "Should have exactly 2 WARNING diagnostics for runtime-only keys");
        }

        // ---- T-V5: Any type* key on EAttribute ----

        /**
         * @MISCONFIG @SPEC(06-type.md#T-V5)
         * Tests that type* keys on EAttribute generate ERROR diagnostics.
         * Type configuration is not applicable to attributes.
         */
        @Test
        @DisplayName("T-V5: type* keys on EAttribute - ERROR diagnostics")
        void validation_typeKeysOnAttribute_errorDiagnostics() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "AttrWithTypeConfigMisplaced");
            EAttribute nameAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "name");

            AspectEntry entry = featureEntry(nameAttr);
            FeatureCodecAspect aspect = (FeatureCodecAspect) entry.getContent();

            // Valid key was parsed
            assertEquals("attr_name", aspect.getEffectiveKey());

            // T-V5: All type* keys → ERROR
            // The test ecore has typeStrategy, typeFormat, typeKey on the attribute
            assertTrue(entry.getDiagnostics().size() >= 3,
                "Should have at least 3 ERROR diagnostics for type* keys on EAttribute (typeStrategy, typeFormat, typeKey)");

            for (MetadataDiagnostic d : entry.getDiagnostics()) {
                assertEquals(DiagnosticSeverity.ERROR, d.getSeverity(),
                    "All type* key diagnostics on EAttribute should be ERROR severity");
                assertTrue(d.getKey().startsWith("type"),
                    "Diagnostic key should start with 'type', got: " + d.getKey());
            }
        }

        // ---- T-V7: typeDiscriminator on EReference ----

        /**
         * @MISCONFIG @SPEC(06-type.md#T-V7)
         * Tests that typeMapping/{mapId} source on EReference generates ERROR diagnostic.
         * Dedicated test with only typeMapping source as the invalid annotation.
         */
        @Test
        @DisplayName("T-V7: typeMapping source on EReference (only) - ERROR diagnostic")
        void validation_typeMappingOnReference_errorDiagnostic() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "RefWithTypeDiscriminatorOnly");
            EReference addressRef = (EReference) EcoreHelper.getFeature(entityClass, "address");

            AspectEntry entry = featureEntry(addressRef);
            ReferenceCodecAspect aspect = (ReferenceCodecAspect) entry.getContent();

            // Valid key was parsed
            assertEquals("addr", aspect.getEffectiveKey());

            // typeMapping source on EReference → ERROR
            assertEquals(1, entry.getDiagnostics().size(),
                "Should have exactly 1 diagnostic for typeMapping source on EReference");
            MetadataDiagnostic diagnostic = entry.getDiagnostics().get(0);
            assertEquals(DiagnosticSeverity.ERROR, diagnostic.getSeverity());
            assertTrue(diagnostic.getMessage().contains("typeMapping"),
                "Diagnostic message should reference typeMapping");
        }

        // ---- T-V30/T-V31: Deprecated typeInclude ----

        /**
         * @MISCONFIG @SPEC(06-type.md#T-V30)
         * Tests that typeInclude on EClass generates WARNING diagnostic.
         * Deprecated: use typeStrategy=NONE instead.
         */
        @Test
        @DisplayName("T-V30: typeInclude on EClass - WARNING diagnostic (deprecated)")
        void validation_deprecatedTypeInclude_warningDiagnostic() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "ClassWithDeprecatedTypeInclude");

            AspectEntry entry = classEntry(entityClass);
            ClassCodecAspect aspect = (ClassCodecAspect) entry.getContent();

            // T-V30: typeInclude → WARNING (deprecated)
            assertEquals(1, entry.getDiagnostics().size(),
                "Should have exactly 1 diagnostic for deprecated typeInclude");
            MetadataDiagnostic diagnostic = entry.getDiagnostics().get(0);
            assertEquals("typeInclude", diagnostic.getKey());
            assertEquals(DiagnosticSeverity.WARNING, diagnostic.getSeverity());
            assertTrue(diagnostic.getMessage().contains("DEPRECATED"),
                "Diagnostic message should mention DEPRECATED");
        }

        /**
         * @MISCONFIG @SPEC(06-type.md#T-V31)
         * Tests that both typeInclude and typeStrategy on EClass generates WARNING diagnostic.
         * typeStrategy takes precedence, typeInclude is ignored.
         */
        @Test
        @DisplayName("T-V31: both typeInclude and typeStrategy - WARNING, strategy takes precedence")
        void validation_bothTypeIncludeAndStrategy_warningDiagnostic() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "ClassWithBothTypeIncludeAndStrategy");

            AspectEntry entry = classEntry(entityClass);
            ClassCodecAspect aspect = (ClassCodecAspect) entry.getContent();

            // Valid typeStrategy should be parsed
            assertNotNull(aspect.getTypeConfig());
            assertEquals(TypeStrategy.NAME, aspect.getTypeConfig().getStrategy());

            // T-V31: Both present → WARNING (typeStrategy takes precedence)
            assertEquals(1, entry.getDiagnostics().size(),
                "Should have exactly 1 diagnostic for deprecated typeInclude when typeStrategy is also set");
            MetadataDiagnostic diagnostic = entry.getDiagnostics().get(0);
            assertEquals("typeInclude", diagnostic.getKey());
            assertEquals(DiagnosticSeverity.WARNING, diagnostic.getSeverity());
            assertTrue(diagnostic.getMessage().contains("DEPRECATED"),
                "Diagnostic message should mention DEPRECATED");
            assertTrue(diagnostic.getMessage().contains("typeStrategy"),
                "Diagnostic message should mention that typeStrategy takes precedence");
        }

        // ---- ID-V11/ID-V12: Runtime-only ID keys in EAnnotation ----

        /**
         * @MISCONFIG @SPEC(09-id.md#ID-V11, ID-V12)
         * Tests that idScope and idFormatScope on EClass generate WARNING diagnostics.
         * These are runtime-only properties, not valid in EAnnotations.
         */
        @Test
        @DisplayName("ID-V11/ID-V12: idScope/idFormatScope on EClass - WARNING diagnostics")
        void validation_idRuntimeOnlyKeysOnClass_warningDiagnostics() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "ClassWithIdRuntimeOnlyKeys");

            AspectEntry entry = classEntry(entityClass);
            ClassCodecAspect aspect = (ClassCodecAspect) entry.getContent();

            // Valid key was parsed
            assertNotNull(aspect.getIdConfig());
            assertEquals(org.eclipse.fennec.codec.metadata.model.codec.IdStrategy.ID_FIELD, aspect.getIdConfig().getStrategy());

            // ID-V11: idScope → WARNING
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "idScope".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.WARNING),
                "Should have WARNING diagnostic for idScope in EAnnotation (ID-V11)");

            // ID-V12: idFormatScope → WARNING
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "idFormatScope".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.WARNING),
                "Should have WARNING diagnostic for idFormatScope in EAnnotation (ID-V12)");

            assertEquals(2, entry.getDiagnostics().size(),
                "Should have exactly 2 WARNING diagnostics for runtime-only ID keys");
        }

        /**
         * @MISCONFIG @SPEC(09-id.md#ID-V11, ID-V12)
         * Tests that idScope and idFormatScope on EReference generate WARNING diagnostics.
         */
        @Test
        @DisplayName("ID-V11/ID-V12: idScope/idFormatScope on EReference - WARNING diagnostics")
        void validation_idRuntimeOnlyKeysOnReference_warningDiagnostics() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "RefWithIdRuntimeOnlyKeys");
            EReference addressRef = (EReference) EcoreHelper.getFeature(entityClass, "address");

            AspectEntry entry = featureEntry(addressRef);
            ReferenceCodecAspect aspect = (ReferenceCodecAspect) entry.getContent();

            // Valid key was parsed
            assertEquals("addr", aspect.getEffectiveKey());

            // ID-V11: idScope → WARNING
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "idScope".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.WARNING),
                "Should have WARNING diagnostic for idScope in EAnnotation (ID-V11)");

            // ID-V12: idFormatScope → WARNING
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "idFormatScope".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.WARNING),
                "Should have WARNING diagnostic for idFormatScope in EAnnotation (ID-V12)");

            assertEquals(2, entry.getDiagnostics().size(),
                "Should have exactly 2 WARNING diagnostics for runtime-only ID keys");
        }
    }

    // ========================================================================
    // Strictness Keys on Wrong Levels (spec 11-feature.md §11)
    // strictOnUnknown/strictOnMissing are class-only → WARNING on features
    // ========================================================================

    @Nested
    @DisplayName("Strictness Keys on Wrong Levels (spec §11)")
    class StrictnessKeysWrongLevels {

        /**
         * @MISCONFIG strictOnUnknown on EAttribute → WARNING diagnostic.
         */
        @Test
        @DisplayName("strictOnUnknown on EAttribute - WARNING diagnostic")
        void misconfig_strictOnUnknownOnAttribute_warningDiagnostic() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "AttrWithStrictOnUnknownMisplaced");
            EAttribute nameAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "name");

            AspectEntry entry = featureEntry(nameAttr);
            FeatureCodecAspect aspect = (FeatureCodecAspect) entry.getContent();

            // Value should NOT be applied (class-only property)
            // Diagnostic should be added
            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "strictOnUnknown".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.WARNING),
                "Should have WARNING diagnostic for strictOnUnknown on EAttribute");
        }

        /**
         * @MISCONFIG strictOnMissing on EReference → WARNING diagnostic.
         */
        @Test
        @DisplayName("strictOnMissing on EReference - WARNING diagnostic")
        void misconfig_strictOnMissingOnReference_warningDiagnostic() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "RefWithStrictOnMissingMisplaced");
            EReference addressRef = (EReference) EcoreHelper.getFeature(entityClass, "address");

            AspectEntry entry = featureEntry(addressRef);
            ReferenceCodecAspect aspect = (ReferenceCodecAspect) entry.getContent();

            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "strictOnMissing".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.WARNING),
                "Should have WARNING diagnostic for strictOnMissing on EReference");
        }

        /**
         * @MISCONFIG Both strictOnUnknown and strictOnMissing on EAttribute → 2 WARNING diagnostics.
         */
        @Test
        @DisplayName("both strictness keys on EAttribute - 2 WARNING diagnostics")
        void misconfig_bothStrictnessOnAttribute_twoWarnings() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "AttrWithBothStrictnessMisplaced");
            EAttribute dataAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "data");

            AspectEntry entry = featureEntry(dataAttr);
            FeatureCodecAspect aspect = (FeatureCodecAspect) entry.getContent();

            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "strictOnUnknown".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.WARNING),
                "Should have WARNING for strictOnUnknown");

            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "strictOnMissing".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.WARNING),
                "Should have WARNING for strictOnMissing");

            assertEquals(2, entry.getDiagnostics().stream()
                    .filter(d -> d.getKey().startsWith("strictOn"))
                    .count(),
                "Should have exactly 2 strictness-related diagnostics");
        }
    }

    // ========================================================================
    // Metadata Merge Keys at Wrong Levels (spec 05-global-options.md §5.7)
    // ========================================================================

    @Nested
    @DisplayName("Metadata Merge Keys at Wrong Levels")
    class MetadataMergeKeysWrongLevels {

        /**
         * @MISCONFIG metadataMerge on EAttribute → ERROR diagnostic.
         */
        @Test
        @DisplayName("metadataMerge on EAttribute - ERROR diagnostic")
        void misconfig_metadataMergeOnAttribute_errorDiagnostic() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "AttrWithMetadataMergeMisplaced");
            EAttribute nameAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "name");

            AspectEntry entry = featureEntry(nameAttr);
            FeatureCodecAspect aspect = (FeatureCodecAspect) entry.getContent();

            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "metadataMerge".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.ERROR),
                "Should have ERROR diagnostic for metadataMerge on EAttribute");
        }

        /**
         * @MISCONFIG metadataKey on EReference → ERROR diagnostic.
         */
        @Test
        @DisplayName("metadataKey on EReference - ERROR diagnostic")
        void misconfig_metadataKeyOnReference_errorDiagnostic() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "RefWithMetadataKeyMisplaced");
            EReference addressRef = (EReference) EcoreHelper.getFeature(entityClass, "address");

            AspectEntry entry = featureEntry(addressRef);
            ReferenceCodecAspect aspect = (ReferenceCodecAspect) entry.getContent();

            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "metadataKey".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.ERROR),
                "Should have ERROR diagnostic for metadataKey on EReference");
        }

        /**
         * @MISCONFIG Both metadataMerge and metadataKey on EAttribute → 2 ERROR diagnostics.
         */
        @Test
        @DisplayName("both metadata merge keys on EAttribute - 2 ERROR diagnostics")
        void misconfig_bothMetadataMergeOnAttribute_twoErrors() {
            EClass entityClass = EcoreHelper.getEClass(testPackage, "AttrWithBothMetadataMergeMisplaced");
            EAttribute dataAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "data");

            AspectEntry entry = featureEntry(dataAttr);
            FeatureCodecAspect aspect = (FeatureCodecAspect) entry.getContent();

            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "metadataMerge".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.ERROR),
                "Should have ERROR for metadataMerge");

            assertTrue(entry.getDiagnostics().stream()
                    .anyMatch(d -> "metadataKey".equals(d.getKey())
                            && d.getSeverity() == DiagnosticSeverity.ERROR),
                "Should have ERROR for metadataKey");

            assertEquals(2, entry.getDiagnostics().stream()
                    .filter(d -> d.getKey().startsWith("metadata"))
                    .count(),
                "Should have exactly 2 metadata-merge-related diagnostics");
        }
    }
}
