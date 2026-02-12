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
package org.eclipse.fennec.model.metadata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for the DiagnosticContainer interface and MetadataDiagnostic model.
 * <p>
 * Verifies that diagnostics can be added at each metadata level and that
 * the allDiagnostics derived feature correctly aggregates diagnostics
 * from contained elements.
 * </p>
 * <p>
 * All tests here are @HELPER tests for infrastructure functionality.
 * </p>
 *
 * @HELPER
 * @SPEC(15-error-handling.md#6.10)
 * @see DiagnosticContainer
 * @see MetadataDiagnostic
 */
@DisplayName("DiagnosticContainer")
class DiagnosticContainerTest {

    private MetadataFactory factory;

    @BeforeEach
    void setUp() {
        factory = MetadataFactory.eINSTANCE;
    }

    private MetadataDiagnostic createDiagnostic(DiagnosticSeverity severity, String message, String key) {
        MetadataDiagnostic diagnostic = factory.createMetadataDiagnostic();
        diagnostic.setSeverity(severity);
        diagnostic.setMessage(message);
        diagnostic.setKey(key);
        return diagnostic;
    }

    // ========================================================================
    // Containment Tests
    // ========================================================================

    @Nested
    @DisplayName("Diagnostics Containment")
    class DiagnosticsContainmentTests {

        @Test
        @DisplayName("diagnostics can be added to FeatureMetadata")
        void testDiagnosticsContainmentOnFeatureMetadata() {
            AttributeMetadata featureMetadata = factory.createAttributeMetadata();
            MetadataDiagnostic diagnostic = createDiagnostic(
                    DiagnosticSeverity.WARNING,
                    "Test warning on feature",
                    "testKey");

            featureMetadata.getDiagnostics().add(diagnostic);

            assertEquals(1, featureMetadata.getDiagnostics().size());
            assertSame(diagnostic, featureMetadata.getDiagnostics().get(0));
            assertSame(featureMetadata, diagnostic.eContainer());
        }

        @Test
        @DisplayName("diagnostics can be added to ClassMetadata")
        void testDiagnosticsContainmentOnClassMetadata() {
            ClassMetadata classMetadata = factory.createClassMetadata();
            MetadataDiagnostic diagnostic = createDiagnostic(
                    DiagnosticSeverity.ERROR,
                    "Test error on class",
                    "typeMapId");

            classMetadata.getDiagnostics().add(diagnostic);

            assertEquals(1, classMetadata.getDiagnostics().size());
            assertSame(diagnostic, classMetadata.getDiagnostics().get(0));
            assertSame(classMetadata, diagnostic.eContainer());
        }

        @Test
        @DisplayName("diagnostics can be added to PackageMetadata")
        void testDiagnosticsContainmentOnPackageMetadata() {
            PackageMetadata packageMetadata = factory.createPackageMetadata();
            MetadataDiagnostic diagnostic = createDiagnostic(
                    DiagnosticSeverity.WARNING,
                    "Test warning on package",
                    null);

            packageMetadata.getDiagnostics().add(diagnostic);

            assertEquals(1, packageMetadata.getDiagnostics().size());
            assertSame(diagnostic, packageMetadata.getDiagnostics().get(0));
            assertSame(packageMetadata, diagnostic.eContainer());
        }

        @Test
        @DisplayName("diagnostic container identifies source element")
        void testDiagnosticContainerIdentifiesSource() {
            ClassMetadata classMetadata = factory.createClassMetadata();
            classMetadata.setName("TestClass");

            MetadataDiagnostic diagnostic = createDiagnostic(
                    DiagnosticSeverity.WARNING,
                    "Annotation key 'typeMapId' is not valid on EReference, ignored",
                    "typeMapId");

            classMetadata.getDiagnostics().add(diagnostic);

            // The container of the diagnostic identifies the source element
            assertTrue(diagnostic.eContainer() instanceof ClassMetadata);
            assertEquals("TestClass", ((ClassMetadata) diagnostic.eContainer()).getName());
        }
    }

    // ========================================================================
    // allDiagnostics Derived Feature Tests
    // ========================================================================

    @Nested
    @DisplayName("allDiagnostics Aggregation")
    class AllDiagnosticsTests {

        @Test
        @DisplayName("allDiagnostics on FeatureMetadata equals diagnostics")
        void testAllDiagnosticsOnFeatureMetadata() {
            AttributeMetadata featureMetadata = factory.createAttributeMetadata();
            MetadataDiagnostic diag1 = createDiagnostic(DiagnosticSeverity.WARNING, "Warning 1", "key1");
            MetadataDiagnostic diag2 = createDiagnostic(DiagnosticSeverity.ERROR, "Error 1", "key2");

            featureMetadata.getDiagnostics().add(diag1);
            featureMetadata.getDiagnostics().add(diag2);

            // For FeatureMetadata, allDiagnostics should equal diagnostics
            assertEquals(2, featureMetadata.getAllDiagnostics().size());
            assertTrue(featureMetadata.getAllDiagnostics().contains(diag1));
            assertTrue(featureMetadata.getAllDiagnostics().contains(diag2));
        }

        @Test
        @DisplayName("allDiagnostics on ClassMetadata includes own + all feature diagnostics")
        void testAllDiagnosticsOnClassMetadata() {
            ClassMetadata classMetadata = factory.createClassMetadata();
            AttributeMetadata attr1 = factory.createAttributeMetadata();
            ReferenceMetadata ref1 = factory.createReferenceMetadata();

            classMetadata.getFeatures().add(attr1);
            classMetadata.getFeatures().add(ref1);

            // Add diagnostics at each level
            MetadataDiagnostic classDiag = createDiagnostic(DiagnosticSeverity.WARNING, "Class warning", "classKey");
            MetadataDiagnostic attrDiag = createDiagnostic(DiagnosticSeverity.WARNING, "Attr warning", "attrKey");
            MetadataDiagnostic refDiag = createDiagnostic(DiagnosticSeverity.ERROR, "Ref error", "refKey");

            classMetadata.getDiagnostics().add(classDiag);
            attr1.getDiagnostics().add(attrDiag);
            ref1.getDiagnostics().add(refDiag);

            // allDiagnostics should include all 3
            assertEquals(3, classMetadata.getAllDiagnostics().size());
            assertTrue(classMetadata.getAllDiagnostics().contains(classDiag));
            assertTrue(classMetadata.getAllDiagnostics().contains(attrDiag));
            assertTrue(classMetadata.getAllDiagnostics().contains(refDiag));

            // Direct diagnostics should only have 1
            assertEquals(1, classMetadata.getDiagnostics().size());
        }

        @Test
        @DisplayName("allDiagnostics on PackageMetadata includes own + all class allDiagnostics")
        void testAllDiagnosticsOnPackageMetadata() {
            PackageMetadata packageMetadata = factory.createPackageMetadata();
            ClassMetadata class1 = factory.createClassMetadata();
            ClassMetadata class2 = factory.createClassMetadata();
            AttributeMetadata attr1 = factory.createAttributeMetadata();

            packageMetadata.getClasses().add(class1);
            packageMetadata.getClasses().add(class2);
            class1.getFeatures().add(attr1);

            // Add diagnostics at each level
            MetadataDiagnostic pkgDiag = createDiagnostic(DiagnosticSeverity.WARNING, "Package warning", null);
            MetadataDiagnostic class1Diag = createDiagnostic(DiagnosticSeverity.WARNING, "Class1 warning", "key1");
            MetadataDiagnostic class2Diag = createDiagnostic(DiagnosticSeverity.ERROR, "Class2 error", "key2");
            MetadataDiagnostic attrDiag = createDiagnostic(DiagnosticSeverity.WARNING, "Attr warning", "key3");

            packageMetadata.getDiagnostics().add(pkgDiag);
            class1.getDiagnostics().add(class1Diag);
            class2.getDiagnostics().add(class2Diag);
            attr1.getDiagnostics().add(attrDiag);

            // allDiagnostics should include all 4
            assertEquals(4, packageMetadata.getAllDiagnostics().size());
            assertTrue(packageMetadata.getAllDiagnostics().contains(pkgDiag));
            assertTrue(packageMetadata.getAllDiagnostics().contains(class1Diag));
            assertTrue(packageMetadata.getAllDiagnostics().contains(class2Diag));
            assertTrue(packageMetadata.getAllDiagnostics().contains(attrDiag));

            // Direct diagnostics should only have 1
            assertEquals(1, packageMetadata.getDiagnostics().size());
        }

        @Test
        @DisplayName("allDiagnostics returns empty list when no diagnostics")
        void testAllDiagnosticsEmpty() {
            PackageMetadata packageMetadata = factory.createPackageMetadata();
            ClassMetadata classMetadata = factory.createClassMetadata();
            AttributeMetadata attrMetadata = factory.createAttributeMetadata();

            packageMetadata.getClasses().add(classMetadata);
            classMetadata.getFeatures().add(attrMetadata);

            assertTrue(packageMetadata.getAllDiagnostics().isEmpty());
            assertTrue(classMetadata.getAllDiagnostics().isEmpty());
            assertTrue(attrMetadata.getAllDiagnostics().isEmpty());
        }
    }

    // ========================================================================
    // MetadataDiagnostic Tests
    // ========================================================================

    @Nested
    @DisplayName("MetadataDiagnostic")
    class MetadataDiagnosticTests {

        @Test
        @DisplayName("default severity is WARNING")
        void testDefaultSeverity() {
            MetadataDiagnostic diagnostic = factory.createMetadataDiagnostic();
            assertEquals(DiagnosticSeverity.WARNING, diagnostic.getSeverity());
        }

        @Test
        @DisplayName("all properties can be set")
        void testAllProperties() {
            MetadataDiagnostic diagnostic = factory.createMetadataDiagnostic();

            diagnostic.setSeverity(DiagnosticSeverity.ERROR);
            diagnostic.setMessage("Test error message");
            diagnostic.setKey("testKey");

            assertEquals(DiagnosticSeverity.ERROR, diagnostic.getSeverity());
            assertEquals("Test error message", diagnostic.getMessage());
            assertEquals("testKey", diagnostic.getKey());
        }
    }
}
